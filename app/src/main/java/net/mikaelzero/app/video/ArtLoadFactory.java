package net.mikaelzero.app.video;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.lifecycle.LifecycleOwner;

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory;
import net.mikaelzero.mojito.loader.ContentLoader;
import net.mikaelzero.mojito.view.sketch.SketchContentLoaderImpl;
import net.mikaelzero.mojito.view.sketch.core.SketchImageView;

import org.jetbrains.annotations.NotNull;
import org.salient.artplayer.ui.VideoView;

import java.io.File;


public class ArtLoadFactory implements ImageViewLoadFactory {


    @Override
    public void loadSillContent(@NotNull View view, @NotNull Uri uri) {
        if (view instanceof ImageView) {
            ((ImageView) view).setImageURI(uri);
        }
    }

    @NotNull
    @Override
    public ContentLoader newContentLoader() {
        return new ArtplayerLoadImpl();
    }
}
