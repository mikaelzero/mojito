package net.mikaelzero.mojito.interfaces;

import net.mikaelzero.mojito.MojitoView;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/17 1:33 PM
 * @Description:
 */
public interface OnMojitoViewCallback {
    void onDrag(MojitoView view, float moveX, float moveY);

    void showFinish(MojitoView mojitoView, boolean showImmediately);

    void onMojitoViewFinish();

    void onRelease(boolean isToMax, boolean isToMin);

    void onLock(boolean isLock);

    void onLongImageMove(float ratio);
}
