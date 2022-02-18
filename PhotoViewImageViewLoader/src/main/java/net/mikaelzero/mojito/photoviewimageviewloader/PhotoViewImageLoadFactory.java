package net.mikaelzero.mojito.photoviewimageviewloader;

import android.net.Uri;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory;
import net.mikaelzero.mojito.loader.ContentLoader;

import org.jetbrains.annotations.NotNull;


public class PhotoViewImageLoadFactory implements ImageViewLoadFactory {
    @Override
    public void loadSillContent(@NotNull View view, @NotNull Uri uri) {
        if (view instanceof PhotoView) {
            ((PhotoView) view).setImageURI(uri);
        }
    }

    @Override
    public void loadContentFail(@NotNull View view, int drawableResId) {
        if (view instanceof PhotoView) {
            ((PhotoView) view).setImageResource(drawableResId);
        }
    }

    @NotNull
    @Override
    public ContentLoader newContentLoader() {
        return new PhotoViewContentLoaderImpl();
    }
}
