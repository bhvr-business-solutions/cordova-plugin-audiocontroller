package com.mokto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theo on 2017-12-01.
 */

public class PlaybackState {
    PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();

    public void update(MusicControlsInfos infos) {

        builder
                .setState(PlaybackStateCompat.STATE_PLAYING, 1, 1)
                .setActions(getAvailableActions(infos));
    }

    public PlaybackStateCompat get() {
        return builder.build();
    }


    private long getAvailableActions(MusicControlsInfos infos) {
        long actions = PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH;
//        if (mPlayingQueue == null || mPlayingQueue.isEmpty()) {
//            return actions;
//        }
//        if (mPlayback.isPlaying()) {
//            actions |= PlaybackState.ACTION_PAUSE;
//        }
//        if (mCurrentIndexOnQueue > 0) {
//            actions |= PlaybackState.ACTION_SKIP_TO_PREVIOUS;
//        }
//        if (mCurrentIndexOnQueue < mPlayingQueue.size() - 1) {
//            actions |= PlaybackState.ACTION_SKIP_TO_NEXT;
//        }
        return actions;
    }
}
