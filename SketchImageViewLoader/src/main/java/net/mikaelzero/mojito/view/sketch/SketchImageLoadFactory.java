package net.mikaelzero.mojito.view.sketch;

import android.net.Uri;
import android.view.View;

import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory;
import net.mikaelzero.mojito.loader.ContentLoader;
import net.mikaelzero.mojito.view.sketch.core.Sketch;
import net.mikaelzero.mojito.view.sketch.core.SketchImageView;

import org.jetbrains.annotations.NotNull;


public class SketchImageLoadFactory implements ImageViewLoadFactory {
    @Override
    public void loadSillContent(@NotNull View view, @NotNull Uri uri) {
        if (view instanceof SketchImageView) {
            Sketch.with(view.getContext()).display(uri.getPath(), (SketchImageView) view).loadingImage((context, sketchView, displayOptions) -> {
                return ((SketchImageView) view).getDrawable(); // 解决缩略图切换到原图显示的时候会闪烁的问题
            }).commit();
        }
    }

    @Override
    public void loadContentFail(@NotNull View view, int drawableResId) {
        if (view instanceof SketchImageView) {
            ((SketchImageView) view).displayResourceImage(drawableResId);
        }
    }

    @NotNull
    @Override
    public ContentLoader newContentLoader() {
        return new SketchContentLoaderImpl();
    }
}
