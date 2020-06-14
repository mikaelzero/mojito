package net.mikaelzero.app;

import android.net.Uri;
import android.view.View;

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import me.panpf.sketch.SketchImageView;

public class SketchImageViewLoadFactory implements ImageViewLoadFactory {

    @Override
    public void loadAnimatedContent(@NotNull View view, int imageType, @NotNull File imageFile) {

    }

    @Override
    public void loadSillContent(@NotNull View view, @NotNull Uri uri) {
        if (view instanceof SketchImageView) {
            ((SketchImageView) view).displayImage(uri.getPath());
        }
    }
}
