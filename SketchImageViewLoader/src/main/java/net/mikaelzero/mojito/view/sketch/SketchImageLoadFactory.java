package net.mikaelzero.mojito.view.sketch;

import android.net.Uri;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory;
import net.mikaelzero.mojito.loader.ContentLoader;
import net.mikaelzero.mojito.view.sketch.core.SketchImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public class SketchImageLoadFactory implements ImageViewLoadFactory {



    @Override
    public void loadAnimatedContent(@NotNull View view, int imageType, @NotNull File imageFile) {

    }

    @Override
    public void loadSillContent(@NotNull View view, @NotNull Uri uri) {
        if (view instanceof SketchImageView) {
            ((SketchImageView) view).displayImage(uri.getPath());
        }
    }

    @NotNull
    @Override
    public ContentLoader newContentLoader(@NotNull LifecycleOwner lifecycleOwner) {
        return new SketchContentLoaderImpl(lifecycleOwner);
    }
}
