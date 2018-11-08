package net.moyokoo.diooto.interfaces;

import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

public interface IIndicator {

    void attach(FrameLayout parent);

    void onShow(ViewPager viewPager);

    /**
     * 拖动的时候  移动的 X 和 Y 距离
     */
    void move(float moveX, float moveY);

    /**
     * 手指松开后的状态
     */
    void fingerRelease(boolean isToMax, boolean isToMin);
}
