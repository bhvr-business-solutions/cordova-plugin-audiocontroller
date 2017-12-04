#import <Cordova/CDVPlugin.h>

@interface AudioController : CDVPlugin {
}

@property NSString * latestEventCallbackId;

- (void)subscribe:(CDVInvokedUrlCommand *)command;
- (void)setControls:(CDVInvokedUrlCommand *)command;
//- (void)updateIsPlaying:(CDVInvokedUrlCommand *)command;
//- (void)unsetControls:(CDVInvokedUrlCommand *)command;

@end
