package net.mikaelzero.mojito.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import net.mikaelzero.mojito.interfaces.IIndicator;
import net.mikaelzero.mojito.tools.Utils;

import org.w3c.dom.Text;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/13 5:39 PM
 * @Description:
 */
public class NumIndicator implements IIndicator {

    private TextView numTv;
    private int originBottomMargin = 10;
    private int currentBottomMargin = originBottomMargin;

    @Override
    public void attach(FrameLayout parent) {
        originBottomMargin = Utils.dip2px(parent.getContext(), 16);
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(parent.getContext(), 36));
        indexLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        indexLp.bottomMargin = originBottomMargin;

        numTv = new TextView(parent.getContext());
        numTv.setGravity(Gravity.CENTER_VERTICAL);
        numTv.setLayoutParams(indexLp);
        numTv.setTextColor(Color.WHITE);
        numTv.setTextSize(16);
        parent.addView(numTv);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        numTv.setVisibility(View.VISIBLE);
        if (viewPager.getAdapter() != null) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    String str = (position + 1) + "/" + viewPager.getAdapter().getCount();
                    numTv.setText(str);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            String firstStr = (viewPager.getCurrentItem() + 1) + "/" + viewPager.getAdapter().getCount();
            numTv.setText(firstStr);
        }
    }


    @Override
    public void move(float moveX, float moveY) {
        if (numTv == null) {
            return;
        }
        FrameLayout.LayoutParams indexLp = (FrameLayout.LayoutParams) numTv.getLayoutParams();
        currentBottomMargin = Math.round(originBottomMargin - moveY / 6f);
        if (currentBottomMargin > originBottomMargin) {
            currentBottomMargin = originBottomMargin;
        }
        indexLp.bottomMargin = currentBottomMargin;
        numTv.setLayoutParams(indexLp);
    }

    @Override
    public void fingerRelease(boolean isToMax, boolean isToMin) {
        if (numTv == null) {
            return;
        }
        int begin = 0;
        int end = 0;
        if (isToMax) {
            begin = currentBottomMargin;
            end = originBottomMargin;
        }
        if (isToMin) {
            numTv.setVisibility(View.GONE);
            return;
        }
        final FrameLayout.LayoutParams indexLp = (FrameLayout.LayoutParams) numTv.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(begin, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indexLp.bottomMargin = (int) animation.getAnimatedValue();
                numTv.setLayoutParams(indexLp);
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
