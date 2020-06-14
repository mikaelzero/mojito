package net.mikaelzero.app;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;

import java.io.File;
public abstract class ImageDownloadTarget implements Target<File>,
        GlideProgressSupport.ProgressListener {

    private Request request;

    private final int width;
    private final int height;

    private final String mUrl;

    protected ImageDownloadTarget(String url) {
        this(SIZE_ORIGINAL, SIZE_ORIGINAL, url);
    }

    private ImageDownloadTarget(int width, int height, String url) {
        this.width = width;
        this.height = height;
        mUrl = url;
    }

    @Override
    public void onResourceReady(@NonNull File resource, Transition<? super File> transition) {
        GlideProgressSupport.forget(mUrl);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        GlideProgressSupport.forget(mUrl);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        GlideProgressSupport.expect(mUrl, this);
    }

    @Override
    public void onLoadFailed(Drawable errorDrawable) {
        GlideProgressSupport.forget(mUrl);
    }

    /**
     * Immediately calls the given callback with the sizes given in the constructor.
     *
     * @param cb {@inheritDoc}
     */
    @Override
    public final void getSize(@NonNull SizeReadyCallback cb) {
        if (!Util.isValidDimensions(width, height)) {
            throw new IllegalArgumentException(
                    "Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given" + " width: "
                            + width + " and height: " + height + ", either provide dimensions in the constructor"
                            + " or call override()");
        }
        cb.onSizeReady(width, height);
    }

    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {
        // Do nothing, we never retain a reference to the callback.
    }

    @Override
    public void setRequest(@Nullable Request request) {
        this.request = request;
    }

    @Override
    @Nullable
    public Request getRequest() {
        return request;
    }

    @Override
    public void onStart() {
        // Do nothing.
    }

    @Override
    public void onStop() {
        // Do nothing.
    }

    @Override
    public void onDestroy() {
        // Do nothing.
    }
}
