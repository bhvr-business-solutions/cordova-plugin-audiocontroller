package com.mokto.audiocontroller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.view.KeyEvent;

public class AudioControllerBroadcastReceiver extends BroadcastReceiver {
	private CallbackContext cb;
	private AudioController audioController;


	public AudioControllerBroadcastReceiver(AudioController audioController){
		this.audioController=audioController;
	}

	public void setCallback(CallbackContext cb){
		this.cb = cb;
	}

	public void stopListening(){
		if (this.cb != null){
			this.cb.success("stop-listening");
			this.cb = null;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (this.cb != null){
			String message = intent.getAction();

			if(message.equals(Intent.ACTION_HEADSET_PLUG)){
				// Headphone plug/unplug
				int state = intent.getIntExtra("state", -1);
				switch (state) {
					case 0:
						this.cb.success("headset-unplugged");
						this.cb = null;
						this.audioController.unregisterMediaButtonEvent();
						break;
					case 1:
						this.cb.success("headset-plugged");
						this.cb = null;
						this.audioController.registerMediaButtonEvent();
						break;
					default:
						break;
				}
			} else if (message.equals("media-button")){
				// Media button
				KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
				if (event.getAction() == KeyEvent.ACTION_DOWN) {

					int keyCode = event.getKeyCode();
					switch (keyCode) {
						case KeyEvent.KEYCODE_MEDIA_NEXT:
							this.cb.success("next");
							break;
						case KeyEvent.KEYCODE_MEDIA_PAUSE:
							this.cb.success("pause");
							break;
						case KeyEvent.KEYCODE_MEDIA_PLAY:
							this.cb.success("play");
							break;
						case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
							this.cb.success("play-pause");
							break;
						case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
							this.cb.success("previous");
							break;
						case KeyEvent.KEYCODE_MEDIA_STOP:
							this.cb.success("stop");
							break;
						case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
							this.cb.success("fast-forward");
							break;
						case KeyEvent.KEYCODE_MEDIA_REWIND:
							this.cb.success("rewind");
							break;
						case KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD:
							this.cb.success("skip-backward");
							break;
						case KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD:
							this.cb.success("skip-forward");
							break;
						case KeyEvent.KEYCODE_MEDIA_STEP_BACKWARD:
							this.cb.success("step-backward");
							break;
						case KeyEvent.KEYCODE_MEDIA_STEP_FORWARD:
							this.cb.success("step-forward");
							break;
						case KeyEvent.KEYCODE_META_LEFT:
							this.cb.success("meta-left");
							break;
						case KeyEvent.KEYCODE_META_RIGHT:
							this.cb.success("meta-right");
							break;
						case KeyEvent.KEYCODE_MUSIC:
							this.cb.success("music");
							break;
						case KeyEvent.KEYCODE_VOLUME_UP:
							this.cb.success("volume-up");
							break;
						case KeyEvent.KEYCODE_VOLUME_DOWN:
							this.cb.success("volume-down");
							break;
						case KeyEvent.KEYCODE_VOLUME_MUTE:
							this.cb.success("volume-mute");
							break;
						case KeyEvent.KEYCODE_HEADSETHOOK:
							this.cb.success("headset-hook");
							break;
						default:
							this.cb.success(message);
							break;
					}
					this.cb = null;
				}
			} else if (message.equals("destroy")){
				// Close Button
				this.cb.success("destroy");
				this.cb = null;
				this.audioController.destroyPlayerNotification();
			} else {
				this.cb.success(message);
				this.cb = null;
			}


		}

	}
}
