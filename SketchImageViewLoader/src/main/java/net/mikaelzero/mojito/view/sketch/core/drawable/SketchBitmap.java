package net.mikaelzero.mojito.view.sketch.core.drawable;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.mikaelzero.mojito.view.sketch.core.decode.ImageAttrs;
import net.mikaelzero.mojito.view.sketch.core.util.SketchUtils;


public abstract class SketchBitmap {

    @NonNull
    private String key;
    @NonNull
    private String uri;
    @Nullable
    protected Bitmap bitmap;
    @NonNull
    private ImageAttrs attrs;

    protected SketchBitmap(@NonNull Bitmap bitmap, @NonNull String key, @NonNull String uri, @NonNull ImageAttrs attrs) {
        //noinspection ConstantConditions
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is null or recycled");
        }

        this.bitmap = bitmap;
        this.key = key;
        this.uri = uri;
        this.attrs = attrs;
    }

    @Nullable
    public Bitmap getBitmap() {
        return bitmap;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    @NonNull
    public ImageAttrs getAttrs() {
        return attrs;
    }

    @NonNull
    public abstract String getInfo();

    public int getByteCount() {
        return SketchUtils.getByteCount(getBitmap());
    }

    @Nullable
    public Bitmap.Config getBitmapConfig() {
        return bitmap != null ? bitmap.getConfig() : null;
    }
}
