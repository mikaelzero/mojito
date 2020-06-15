package net.mikaelzero.mojito.view.sketch.core.cache.recycle;

import android.graphics.Bitmap;

import net.mikaelzero.mojito.view.sketch.core.Key;


public interface LruPoolStrategy extends Key {
    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    Bitmap removeLast();

    String logBitmap(Bitmap bitmap);

    String logBitmap(int width, int height, Bitmap.Config config);

    int getSize(Bitmap bitmap);
}
