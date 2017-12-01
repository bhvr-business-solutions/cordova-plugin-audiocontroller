package com.mokto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaMetadataCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by theo on 2017-12-01.
 */

public class Metadata {

    private MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
    private String url = null;

    public void update(MusicControlsInfos infos) {
        builder
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, infos.album)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, infos.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, infos.track)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, infos.duration);

    }

    public void updateCover(Bitmap bitmap, String url) {
        if (this.url == null || url != this.url) {
            this.url = url;
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap);
        }
    }

    public MediaMetadataCompat get() {
        return builder.build();
    }

}
