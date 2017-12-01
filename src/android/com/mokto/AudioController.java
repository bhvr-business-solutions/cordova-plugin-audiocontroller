/**
 */
package com.mokto;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;


public class AudioController extends CordovaPlugin {

    private static final String TAG = "AudioController";
    private static final int NOTIFICATION_ID = 100;
    private MediaSessionCompat mSession;
    private Metadata metadata = new Metadata();
    private PlaybackState playbackState = new PlaybackState();
    private Notification notification;

    private CallbackContext callbackContext;
    //An example of returning data back to the web layer
    /*
    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, (new Date()).toString()));
    */

    @Override
    public void initialize(final CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);


        mSession = new MediaSessionCompat(getContext(), TAG);
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        this.notification = new Notification(getContext(), "0");

        Log.d(TAG, "Initializing AudioController");

//        ComponentName mediaButtonReceiver = new ComponentName(getContext(), MediaButtonReceiver.class.getName());
//        final Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
//        mediaButtonIntent.setComponent(mediaButtonReceiver);

    }

    @Override
    public void onDestroy() {
        mSession.setActive(false);
        getNotificationManager().cancel(NOTIFICATION_ID);
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("subscribe")) {
            Log.d(TAG, "Subscribe");
            this.callbackContext = callbackContext;
            return true;

        } else if (action.equals("setControls")) {

            LOG.d(TAG, "Set controls");
            final MusicControlsInfos infos = new MusicControlsInfos(args);
            this.cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    updateNotification(infos);
                }
            });
            return true;

        }
        return false;
    }

    private void updateNotification(MusicControlsInfos infos) {

        Bitmap bitmap = BitmapBuilder.getBitmapCover(infos.cover, cordova.getActivity());

        metadata.update(infos);
        metadata.updateCover(bitmap, infos.cover);
        mSession.setMetadata(metadata.get());

        playbackState.update(infos);
        mSession.setPlaybackState(playbackState.get());

        notification.update(infos, mSession);
        notification.updateCover(bitmap, infos.cover);
        getNotificationManager().notify(NOTIFICATION_ID, notification.get());

        mSession.setActive(true);
    }




    public Context getContext() {
        return cordova.getActivity().getBaseContext();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }




}
