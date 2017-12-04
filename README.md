# Cordova Audio Controller Plugin

## Thanks

This plugin is partly inspired by https://github.com/homerours/cordova-music-controls-plugin repository. It is not updated on NPM anymore so I took the liberty to rewrite objective C code and push it with a different name.

#### Major diffferences

* Removed the listen() method (it is now automatic)
* Fixed a lot of bugs on IOS
* Added a Audio interruption event on IOS
* Removed the updateIsPlaying method and destroy on IOS (for now)

<img src='https://imgur.com/fh3ACOq.png' width='564' height='342'>

Music controls for Cordova applications. Display a 'media' notification with play/pause, previous, next buttons, allowing the user to control the play. Handle also headset event (plug, unplug, headset button).

## Supported platforms
- Android (4.1+)
- iOS 8+

## Installation
`cordova plugin add https://github.com/bhvr-business-solutions/cordova-plugin-audiocontroller`

## Methods
- Replace the media controls:
```javascript
AudioController.setControls({
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


- Subscribe events to the media controller:
```javascript
// Register callback
AudioController.subscribe(function(eventName) {
    
});
```

## List of media button events 
- Default:
```javascript
'play', 'pause',
```

- Android only:
```javascript
'previous', 'next', 'pause', 'play',
'skip-backward', 'skip-forward'
```

- iOS Only:
```javascript
'music-controls-skip-forward', 'music-controls-skip-backward', 'interruption-started', 'interruption-ended', 'seek' (2nd argument available)
```

- Android Only:
```javascript
'fast-forward', 'rewind',
'play-pause', 'stop',
'step-backward', 'step-forward',
'meta-left', 'meta-right', 'music',
'volume-up', 'volume-down', 'volume-mute',
'headset-hook'
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

Thanks !