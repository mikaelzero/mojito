package net.mikaelzero.mojito.interfaces;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import net.mikaelzero.mojito.tools.Utils;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/13 5:39 PM
 * @Description:
 */
public class CircleIndexIndicator implements IIndicator {

    private CircleIndicator circleIndicator;
    private int originBottomMargin = 10;
    private int currentBottomMargin = originBottomMargin;

    @Override
    public void attach(FrameLayout parent) {
        originBottomMargin = Utils.dip2px(parent.getContext(), 16);
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(parent.getContext(), 36));
        indexLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        indexLp.bottomMargin = originBottomMargin;

        circleIndicator = new CircleIndicator(parent.getContext());
        circleIndicator.setGravity(Gravity.CENTER_VERTICAL);
        circleIndicator.setLayoutParams(indexLp);

        parent.addView(circleIndicator);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        circleIndicator.setVisibility(View.VISIBLE);
        circleIndicator.setViewPager(viewPager);
    }


    @Override
    public void move(float moveX, float moveY) {
        if (circleIndicator == null) {
            return;
        }
        FrameLayout.LayoutParams indexLp = (FrameLayout.LayoutParams) circleIndicator.getLayoutParams();
        currentBottomMargin = Math.round(originBottomMargin - moveY / 6f);
        if (currentBottomMargin > originBottomMargin) {
            currentBottomMargin = originBottomMargin;
        }
        indexLp.bottomMargin = currentBottomMargin;
        circleIndicator.setLayoutParams(indexLp);
    }

    @Override
    public void fingerRelease(boolean isToMax, boolean isToMin) {
        if (circleIndicator == null) {
            return;
        }
        int begin = 0;
        int end = 0;
        if (isToMax) {
            begin = currentBottomMargin;
            end = originBottomMargin;
        }
        if (isToMin) {
            circleIndicator.setVisibility(View.GONE);
            return;
        }
        final FrameLayout.LayoutParams indexLp = (FrameLayout.LayoutParams) circleIndicator.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(begin, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indexLp.bottomMargin = (int) animation.getAnimatedValue();
                circleIndicator.setLayoutParams(indexLp);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        valueAnimator.setDuration(300).start();
    }
}
