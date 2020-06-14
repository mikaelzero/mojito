package net.mikaelzero.mojito.loader;

import android.net.Uri;

import androidx.annotation.UiThread;

import java.io.File;

public interface ImageLoader {

    void loadImage(int requestId, Uri uri, Callback callback);

    void prefetch(Uri uri);

    void cancel(int requestId);

    void cancelAll();

    @UiThread
    interface Callback {
        void onCacheHit(int imageType, File image);

        void onCacheMiss(int imageType, File image);

        void onStart();

        void onProgress(int progress);

        void onFinish();

        void onSuccess(File image);

        void onFail(Exception error);
    }
}
