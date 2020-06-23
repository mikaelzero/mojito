package net.mikaelzero.app.video;

import android.content.Context;
import android.util.AttributeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.salient.artplayer.conduction.ScaleType;
import org.salient.artplayer.ui.ResizeTextureView;
import org.salient.artplayer.ui.VideoView;

import java.lang.reflect.Field;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/22 3:29 PM
 * @Description:
 */
public class HackVideoView extends VideoView {
    ResizeTextureView resizeTextureView;

    public HackVideoView(@NotNull Context context) {
        super(context);
    }

    public HackVideoView(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HackVideoView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void setScreenScale(ScaleType type) {
        if (resizeTextureView == null) {
            Field privateStringField = null;
            try {
                privateStringField = VideoView.class.getDeclaredField("textureView");
                privateStringField.setAccessible(true);
                resizeTextureView = (ResizeTextureView) privateStringField.get(this);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (resizeTextureView != null) {
            resizeTextureView.setScreenScale(type);
        }
    }
}
