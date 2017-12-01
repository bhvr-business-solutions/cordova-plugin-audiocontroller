#import "AudioController.h"
#import "MusicControlsInfo.h"

#import <Cordova/CDVAvailability.h>
#import <AudioToolbox/AudioServices.h>
#import <AudioToolbox/AudioToolbox.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>

MusicControlsInfo * musicControlsInfo;

@implementation AudioController

- (void)subscribe:(CDVInvokedUrlCommand *)command {
    NSLog(@"Subscribe");
    [self setLatestEventCallbackId:command.callbackId];
}

- (void)setControls:(CDVInvokedUrlCommand *)command {
    NSLog(@"setControls");
    
    if (!NSClassFromString(@"MPNowPlayingInfoCenter")) {
        return;
    }

    musicControlsInfo = [[MusicControlsInfo alloc] initWithDictionary:[command.arguments objectAtIndex:0]];
    MPMediaItemArtwork *albumArt = [[MPMediaItemArtwork alloc] initWithImage: [UIImage imageWithData:
        [NSData dataWithContentsOfURL: [NSURL URLWithString:[musicControlsInfo cover]]]
    ]];
    
    [self.commandDelegate runInBackground:^{
        MPNowPlayingInfoCenter * nowPlayingInfoCenter =  [MPNowPlayingInfoCenter defaultCenter];
        
        NSMutableDictionary *songInfo = [[NSMutableDictionary alloc] init];
        
        [songInfo setObject:[musicControlsInfo track] forKey:MPMediaItemPropertyTitle];
        [songInfo setObject:[musicControlsInfo artist] forKey:MPMediaItemPropertyArtist];
        [songInfo setObject:albumArt forKey:MPMediaItemPropertyArtwork];
        [songInfo setObject:[NSNumber numberWithBool: [musicControlsInfo isPlaying]] forKey:MPNowPlayingInfoPropertyPlaybackRate];
        [songInfo setObject:[NSNumber numberWithInt:[musicControlsInfo duration]] forKey:MPMediaItemPropertyPlaybackDuration];
        [songInfo setObject:[NSNumber numberWithInt:[musicControlsInfo elapsed]] forKey:MPNowPlayingInfoPropertyElapsedPlaybackTime];
        
        if ([musicControlsInfo album] != nil) {
            [songInfo setObject:[musicControlsInfo album] forKey:MPMediaItemPropertyAlbumTitle];
        }
        
        [nowPlayingInfoCenter setNowPlayingInfo:songInfo];
        [self setCommands];
        
    }];
}

- (MPMediaItemArtwork *) getCover: (NSString *) coverUri {
    UIImage * coverImage = nil;
    
    if (coverUri == nil) {
        return nil;
    }
    
    if ([coverUri hasPrefix:@"http://"] || [coverUri hasPrefix:@"https://"]) {
        NSURL * coverImageUrl = [NSURL URLWithString:coverUri];
        NSData * coverImageData = [NSData dataWithContentsOfURL: coverImageUrl];
        
        coverImage = [UIImage imageWithData: coverImageData];
    }
    else if ([coverUri hasPrefix:@"file://"]) {
        NSString * fullCoverImagePath = [coverUri stringByReplacingOccurrencesOfString:@"file://" withString:@""];
        
        if ([[NSFileManager defaultManager] fileExistsAtPath: fullCoverImagePath]) {
            coverImage = [[UIImage alloc] initWithContentsOfFile: fullCoverImagePath];
        }
    }
    else if (![coverUri isEqual:@""]) {
        NSString * baseCoverImagePath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        NSString * fullCoverImagePath = [NSString stringWithFormat:@"%@%@", baseCoverImagePath, coverUri];
        
        if ([[NSFileManager defaultManager] fileExistsAtPath:fullCoverImagePath]) {
            coverImage = [UIImage imageNamed:fullCoverImagePath];
        }
    }
    else {
        coverImage = [UIImage imageNamed:@"none"];
    }
    
    return [self isCoverImageValid:coverImage] ? [[MPMediaItemArtwork alloc] initWithImage:coverImage] : nil;
}

- (bool) isCoverImageValid: (UIImage *) coverImage {
    return coverImage != nil && ([coverImage CIImage] != nil || [coverImage CGImage] != nil);
}

- (void) setCommands {
    [self unsetCommands];
    
    MPRemoteCommandCenter *commandCenter = [MPRemoteCommandCenter sharedCommandCenter];
    
    [commandCenter.playCommand setEnabled:true];
    [commandCenter.playCommand addTarget:self action:@selector(play:)];
    [commandCenter.pauseCommand setEnabled:true];
    [commandCenter.pauseCommand addTarget:self action:@selector(pause:)];
    
    if(musicControlsInfo.hasNext){
        [commandCenter.nextTrackCommand setEnabled:true];
        [commandCenter.nextTrackCommand addTarget:self action:@selector(next:)];
    }
    if(musicControlsInfo.hasPrev){
        [commandCenter.previousTrackCommand setEnabled:true];
        [commandCenter.previousTrackCommand addTarget:self action:@selector(previous:)];
    }

    //Some functions are not available in earlier versions
    if(floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_9_0){
        if(musicControlsInfo.hasSkipForward){
            commandCenter.skipForwardCommand.preferredIntervals = @[@(musicControlsInfo.skipForwardInterval)];
            [commandCenter.skipForwardCommand setEnabled:true];
            [commandCenter.skipForwardCommand addTarget: self action:@selector(skipForward:)];
        }
        if(musicControlsInfo.hasSkipBackward){
            commandCenter.skipBackwardCommand.preferredIntervals = @[@(musicControlsInfo.skipForwardInterval)];
            [commandCenter.skipBackwardCommand setEnabled:true];
            [commandCenter.skipBackwardCommand addTarget: self action:@selector(skipBackward:)];
        }
        if(musicControlsInfo.hasScrubbing){
            [commandCenter.changePlaybackPositionCommand setEnabled:true];
            [commandCenter.changePlaybackPositionCommand addTarget:self action:@selector(changedThumbSliderOnLockScreen:)];
        }
    }
}

- (void) unsetCommands {
    MPRemoteCommandCenter *commandCenter = [MPRemoteCommandCenter sharedCommandCenter];
    
    [commandCenter.playCommand removeTarget:self];
    [commandCenter.pauseCommand removeTarget:self];
    [commandCenter.nextTrackCommand removeTarget:self];
    [commandCenter.previousTrackCommand removeTarget:self];
    
    //Some functions are not available in earlier versions
    if(floor(NSFoundationVersionNumber) > NSFoundationVersionNumber_iOS_9_0){
        [commandCenter.skipForwardCommand removeTarget:self];
        [commandCenter.skipBackwardCommand removeTarget:self];
        [commandCenter.changePlaybackPositionCommand removeTarget:self];
    }
}





/**
 * EVENTS
 */

- (void)sendEvent:(NSString *) eventName {
    NSLog(@"SEND EVENT %@", eventName);
    if ([self latestEventCallbackId] == nil) {
        return;
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:eventName];
    [self.commandDelegate sendPluginResult:result callbackId:[self latestEventCallbackId]];
}

- (void)sendSeekEvent:(double) positionTime {
    NSLog(@"SEEK TO %f", positionTime);
    if ([self latestEventCallbackId] == nil) {
        return;
    }
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[NSString stringWithFormat: @"seek|%f", positionTime]];
    [self.commandDelegate sendPluginResult:result callbackId:[self latestEventCallbackId]];
}

- (void)pluginInitialize {
    // INTERRUPTION
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(interruption:) name:AVAudioSessionInterruptionNotification object: [AVAudioSession sharedInstance]];
    
    // REGISTER FOR CONTROL CENTER ACTIONS
    
    
    
}

- (void) voidRemoteCommandEvent:(MPRemoteCommandEvent *)event {
    return;
}


- (void) play:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"play"];
    return;
}
- (void) pause:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"pause"];
    return;
}
- (void) next:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"next"];
    return;
}
- (void) previous:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"previous"];
    return;
}
- (void) skipForward:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"skip-forward"];
    return;
}
- (void) skipBackward:(MPRemoteCommandEvent *)event {
    [self sendEvent:@"skip-backward"];
    return;
}
- (void) changedThumbSliderOnLockScreen:(MPChangePlaybackPositionCommandEvent *)event {
    [self sendSeekEvent:event.positionTime];
    return;
}

- (void)interruption:(NSNotification*)notification {
    
    NSDictionary *interruptionDict = notification.userInfo;
    NSInteger interruptionType = [[interruptionDict valueForKey:AVAudioSessionInterruptionTypeKey] integerValue];
    // decide what to do based on interruption type here...
    switch (interruptionType) {
        case AVAudioSessionInterruptionTypeBegan:
            [self sendEvent:@"interruption-started"];
            break;
            
        case AVAudioSessionInterruptionTypeEnded:
            [self sendEvent: @"interruption-ended"];
            break;
            
        default:
            NSLog(@"Audio Session Interruption Notification case default.");
            break;
    }
}

@end
