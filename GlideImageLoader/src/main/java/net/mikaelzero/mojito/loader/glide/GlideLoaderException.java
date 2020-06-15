package net.mikaelzero.mojito.loader.glide;

import android.graphics.drawable.Drawable;

public class GlideLoaderException extends RuntimeException {
    private final Drawable mErrorDrawable;

    public GlideLoaderException(final Drawable errorDrawable) {
        mErrorDrawable = errorDrawable;
    }

    public Drawable getErrorDrawable() {
        return mErrorDrawable;
    }
}
