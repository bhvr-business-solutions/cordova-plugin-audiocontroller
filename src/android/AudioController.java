package com.mokto.audiocontroller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Build;
import android.R;
import android.content.BroadcastReceiver;
import android.media.AudioManager;

public class AudioController extends CordovaPlugin {
	private AudioControllerBroadcastReceiver mMessageReceiver;
	private AudioControllerNotification notification;
	private final int notificationID=7824;
	private AudioManager mAudioManager;
	private PendingIntent mediaButtonPendingIntent;
	private boolean mediaButtonAccess=true;


	private void registerBroadcaster(AudioControllerBroadcastReceiver mMessageReceiver){
		final Context context = this.cordova.getActivity().getApplicationContext();
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("previous"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("pause"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("play"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("next"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("media-button"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("destroy"));

		// Listen for headset plug/unplug
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}

	// Register pendingIntent for broacast
	public void registerMediaButtonEvent(){
		if (this.mediaButtonAccess && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
			this.mAudioManager.registerMediaButtonEventReceiver(this.mediaButtonPendingIntent);
		}
	}

	public void unregisterMediaButtonEvent(){
		if (this.mediaButtonAccess && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
			this.mAudioManager.unregisterMediaButtonEventReceiver(this.mediaButtonPendingIntent);
		}
	}

	public void destroyPlayerNotification(){
		this.notification.destroy();
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		final Activity activity = this.cordova.getActivity();
		final Context context=activity.getApplicationContext();

		this.notification = new AudioControllerNotification(activity,this.notificationID);
		this.mMessageReceiver = new AudioControllerBroadcastReceiver(this);
		this.registerBroadcaster(mMessageReceiver);

		// Register media (headset) button event receiver
		try {
			this.mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			Intent headsetIntent = new Intent("media-button");
			this.mediaButtonPendingIntent = PendingIntent.getBroadcast(context, 0, headsetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			this.registerMediaButtonEvent();
		} catch (Exception e) {
			this.mediaButtonAccess=false;
			e.printStackTrace();
		}

		// Notification Killer
		ServiceConnection mConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder binder) {
				((KillBinder) binder).service.startService(new Intent(activity, AudioControllerNotificationKiller.class));
			}
			public void onServiceDisconnected(ComponentName className) {
			}
		};
		Intent startServiceIntent = new Intent(activity,AudioControllerNotificationKiller.class);
		startServiceIntent.putExtra("notificationID",this.notificationID);
		activity.bindService(startServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		final Context context=this.cordova.getActivity().getApplicationContext();
		final Activity activity=this.cordova.getActivity();

		if (action.equals("setControls")) {
			final AudioControllerInfos infos = new AudioControllerInfos(args);
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					notification.updateNotification(infos);
					callbackContext.success("success");
				}
			});
		}
		else if (action.equals("updateIsPlaying")){
			final JSONObject params = args.getJSONObject(0);
			final boolean isPlaying = params.getBoolean("isPlaying");
			this.notification.updateIsPlaying(isPlaying);
			callbackContext.success("success");
		}
		else if (action.equals("updateDismissable")){
			final JSONObject params = args.getJSONObject(0);
			final boolean dismissable = params.getBoolean("dismissable");
			this.notification.updateDismissable(dismissable);
			callbackContext.success("success");
		}
		else if (action.equals("unsetControls")){
			this.notification.destroy();
			this.mMessageReceiver.stopListening();
			callbackContext.success("success");
		}
		else if (action.equals("subscribe")) {
			this.registerMediaButtonEvent();
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					mMessageReceiver.setCallback(callbackContext);
				}
			});
		}
		return true;
	}

	@Override
	public void onDestroy() {
		this.notification.destroy();
		this.mMessageReceiver.stopListening();
		this.unregisterMediaButtonEvent();
		super.onDestroy();
	}

	@Override
	public void onReset() {
		onDestroy();
		super.onReset();
	}
}
