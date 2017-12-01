# Cordova Audio Controller Plugin

<img src='https://imgur.com/fh3ACOq.png' width='564' height='342'>

Music controls for Cordova applications. Display a 'media' notification with play/pause, previous, next buttons, allowing the user to control the play. Handle also headset event (plug, unplug, headset button).

## Supported platforms
- Android (4.1+)
- iOS 8+ (by [0505gonzalez](https://github.com/0505gonzalez))

## Installation
`cordova plugin add https://github.com/homerours/cordova-music-controls-plugin`

## Methods
- Create the media controls:
```javascript
AudioController.set({
    track       : 'Time is Running Out',		// optional, default : ''
	artist      : 'Muse',						// optional, default : ''
    cover       : 'albums/absolution.jpg',		// optional, default : nothing
	// cover can be a local path (use fullpath 'file:///storage/emulated/...', or only 'my_image.jpg' if my_image.jpg is in the www folder of your app)
	//			 or a remote url ('http://...', 'https://...', 'ftp://...')
	isPlaying   : true,							// optional, default : true

	// hide previous/next/close buttons:
	hasPrev   : true,		// show previous button, optional, default: true
	hasNext   : true,		// show next button, optional, default: true
    album       : 'Absolution',     // optional, default: ''
    
});

```
<!-- 
- Destroy the media controller:
```javascript
MusicControls.destroy(onSuccess, onError);
``` -->

- Subscribe events to the media controller:
```javascript
// Register callback
AudioController.subscribe(function(eventName) {
    
});
```

<!-- - Toggle play/pause:
```javascript
MusicControls.updateIsPlaying(true); // toggle the play/pause notification button
MusicControls.updateDismissable(true);
``` -->

<!-- - iOS Specific Events:
Allows you to listen for iOS events fired from the scrubber in control center.
```javascript
MusicControls.updateElapsed({
	elapsed: 208, // seconds
	isPlaying: true
});
``` -->

## List of media button events 
- Default:
```javascript
'play', 'pause',
```

- Android only:
```javascript
'music-controls-media-button-next', 'music-controls-media-button-pause', 'music-controls-media-button-play',
'music-controls-media-button-play-pause', 'music-controls-media-button-previous', 'music-controls-media-button-stop',
'music-controls-media-button-fast-forward', 'music-controls-media-button-rewind', 'music-controls-media-button-skip-backward',
'music-controls-media-button-skip-forward', 'music-controls-media-button-step-backward', 'music-controls-media-button-step-forward',
'music-controls-media-button-meta-left', 'music-controls-media-button-meta-right', 'music-controls-media-button-music',
'music-controls-media-button-volume-up', 'music-controls-media-button-volume-down', 'music-controls-media-button-volume-mute',
'music-controls-media-button-headset-hook'
```

- iOS Only:
```javascript
'music-controls-skip-forward', 'music-controls-skip-backward'
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request