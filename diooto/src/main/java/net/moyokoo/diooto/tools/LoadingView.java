package net.moyokoo.diooto.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class LoadingView extends View {
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint circleBgPaint;
    private double percent = 0.083;
    private float interval;
    private float radius;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        if (null == attrs) {
            if (!(getLayoutParams() instanceof FrameLayout.LayoutParams)) {
                FrameLayout.LayoutParams layoutParams =
                        new FrameLayout.LayoutParams(
                                dip2Px(getContext(),50),
                                dip2Px(getContext(),50),
                                Gravity.CENTER);
                setLayoutParams(layoutParams);
            }
        }
        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setColor(Color.WHITE);
        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setColor(Color.WHITE);
        circleBgPaint = new Paint();
        circleBgPaint.setAntiAlias(true);
        circleBgPaint.setStyle(Paint.Style.FILL);
        circleBgPaint.setColor(Color.parseColor("#80000000"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        radius = (getWidth() >= getHeight() ? getHeight() : getWidth()) * 0.8f;
        interval = (float) (radius * 0.2);
        mPaint2.setStrokeWidth(interval / 6);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius / 2 - interval / 3, circleBgPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius / 2 - interval / 3, mPaint2);
        RectF localRect = new RectF(
                getWidth() / 2 - radius / 2 + interval,
                getHeight() / 2 - radius / 2 + interval,
                getWidth() / 2 + radius / 2 - interval,
                getHeight() / 2 + radius / 2 - interval);
        float f1 = (float) (percent * 360);
        canvas.drawArc(localRect, -90, f1, true, mPaint1);
    }

    public void setProgress(double progress) {
        progress = progress / 100f;
        if (progress == 0) {
            progress = 0.083;
        }
        setVisibility(VISIBLE);
        this.percent = progress;
        invalidate();//重新执行onDraw方法,重新绘制图形
    }

    public void loadCompleted() {
        setVisibility(GONE);
    }

    public void loadFaild() {
        setProgress(1.0);
        setVisibility(GONE);
    }

    public void setOutsideCircleColor(int color) {
        mPaint2.setColor(color);
    }

    public void setInsideCircleColor(int color) {
        mPaint1.setColor(color);
    }

    public void setTargetView(View target) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }

        if (target == null) {
            return;
        }

        if (target.getParent() instanceof FrameLayout) {
            ((FrameLayout) target.getParent()).addView(this);

        } else if (target.getParent() instanceof ViewGroup) {
            ViewGroup parentContainer = (ViewGroup) target.getParent();
            int groupIndex = parentContainer.indexOfChild(target);
            parentContainer.removeView(target);

            FrameLayout badgeContainer = new FrameLayout(getContext());
            ViewGroup.LayoutParams parentLayoutParams = target.getLayoutParams();

            badgeContainer.setLayoutParams(parentLayoutParams);
            target.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams);
            badgeContainer.addView(target);

            badgeContainer.addView(this);
        } else if (target.getParent() == null) {

        }

    }

    /*
     * converts dip to px
     */
    public static int dip2Px(Context context,float dip) {
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}