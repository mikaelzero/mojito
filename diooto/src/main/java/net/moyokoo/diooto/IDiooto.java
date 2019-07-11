package net.moyokoo.diooto;

import android.view.MotionEvent;
import android.view.View;

public interface IDiooto {
    void handleLongImage();
    boolean needEvent(MotionEvent event,View contentView);
    void move(View contentView);
    //releaseLeft releaseWidth releaseY releaseHeight
    int[] providerReleasePoint();

    //endWidth  endHeight
    int[] providerEndSize();
    void initContentView(View contentView);
    void changeImageViewToFitCenter();
    void changeImageViewToCenterCrop();
}
