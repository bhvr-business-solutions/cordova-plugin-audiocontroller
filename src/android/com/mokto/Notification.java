package com.mokto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theo on 2017-12-01.
 */

public class Notification {
    private NotificationCompat.Builder builder;
    private Context context;
    private String url = null;

    public Notification(Context context, String channelId) {
        this.builder = new NotificationCompat.Builder(context, channelId);
        this.context = context;
    }

    public void update(MusicControlsInfos infos, MediaSessionCompat mediaSession) {

        MediaControllerCompat controller = mediaSession.getController();
//        MediaMetadataCompat mediaMetadata = controller.getMetadata();

        MediaStyle style = new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP));

        builder.setContentTitle(infos.track)
                .setContentText(infos.artist)
                .setSubText(infos.album)

                // Enable launching the player by clicking the notification
                .setContentIntent(controller.getSessionActivity())
                // Stop the service when the notification is swiped away
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Add an app icon and set its accent color
                // Be careful about the color
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setColor(ContextCompat.getColor(context, android.R.color.background_dark))



                .addAction(new NotificationCompat.Action(
                        android.R.drawable.ic_media_pause, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .setStyle(style);

    }

    public void updateCover(Bitmap bitmap, String url) {
        if (this.url == null || url != this.url) {
            this.url = url;
            builder.setLargeIcon(bitmap);
        }
    }

    public android.app.Notification get() {
        return builder.build();
    }


}
