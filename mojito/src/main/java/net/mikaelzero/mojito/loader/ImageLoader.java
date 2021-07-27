package net.mikaelzero.mojito.loader;

import android.net.Uri;

import androidx.annotation.UiThread;

import java.io.File;

public interface ImageLoader {

    void loadImage(int requestId, Uri uri, boolean onlyRetrieveFromCache,Callback callback);

    void prefetch(Uri uri);

    void cancel(int requestId);

    void cancelAll();

    void cleanCache();

    @UiThread
    interface Callback {

        void onStart();

        void onProgress(int progress);

        void onFinish();

        void onSuccess(File image);

        void onFail(Exception error);
    }
}
