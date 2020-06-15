package net.mikaelzero.mojito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import net.mikaelzero.mojito.loader.ContentLoader;
import net.mikaelzero.mojito.loader.IMojitoConfig;
import net.mikaelzero.mojito.tools.MarginViewWrapper;
import net.mikaelzero.mojito.tools.ScreenUtils;

/**
 * Created by mikaelzero.
 * Date:  2017/2/17
 * mod: 2020/6/6
 */
public class MojitoView extends FrameLayout {


    private float mAlpha = 0;
    private float mDownX;
    private float mDownY;
    private float mYDistanceTraveled;
    private float mMoveDownTranslateY;
    private float mTranslateX;

    private final float DEFAULT_MIN_SCALE = 0.3f;
    private int MAX_TRANSLATE_Y;
    private int MAX_Y;

    FrameLayout contentLayout;
    View backgroundView;

    private final long DEFAULT_DURATION = 300;
    long animationDuration = DEFAULT_DURATION;
    private int mOriginLeft;
    private int mOriginTop;
    private int mOriginHeight;
    private int mOriginWidth;

    private int screenWidth;
    private int screenHeight;
    private int targetImageTop;
    private int targetImageWidth;
    private int targetImageHeight;

    private int mLastY;

    int minWidth = 0;
    int minHeight = 0;

    int releaseLeft = 0;
    float releaseY = 0;
    int releaseWidth = 0;
    int releaseHeight = 0;
    int realWidth;
    int realHeight;
    int touchSlop = ViewConfiguration.getTouchSlop();

    int imageLeftOfAnimatorEnd = 0;
    int imageTopOfAnimatorEnd = 0;
    int imageWidthOfAnimatorEnd = 0;
    int imageHeightOfAnimatorEnd = 0;

    MarginViewWrapper imageWrapper;
    boolean isDrag = false;
    boolean isAnimating = false;
    boolean isMultiFinger = false;

    ContentLoader contentLoader;

    public MojitoView(Context context) {
        this(context, null);
    }

    public MojitoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MojitoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenWidth = ScreenUtils.getScreenWidth(context);
        screenHeight = ScreenUtils.getScreenHeight(context);
        MAX_TRANSLATE_Y = screenHeight / 8;
        MAX_Y = screenHeight - screenHeight / 20;

        addView(LayoutInflater.from(getContext()).inflate(R.layout.content_item, null), 0);
        contentLayout = findViewById(R.id.contentLayout);
        backgroundView = findViewById(R.id.backgroundView);
        backgroundView.setAlpha(mAlpha);
        imageWrapper = new MarginViewWrapper(contentLayout);
    }


    public void putData(int left, int top, int originWidth, int originHeight, int realWidth, int realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        mOriginLeft = left;
        mOriginTop = top;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
    }

    public void show(boolean showImmediately) {
        mAlpha = showImmediately ? mAlpha = 1f : 0f;
        setVisibility(View.VISIBLE);
        getLocation(showImmediately);
    }


    private void getLocation(final boolean showImmediately) {
        int[] locationImage = new int[2];
        contentLayout.getLocationOnScreen(locationImage);
        int endLeft = 0;
        if (screenWidth / (float) screenHeight < realWidth / (float) realHeight) {
            targetImageWidth = screenWidth;
            targetImageHeight = (int) (targetImageWidth * (realHeight / (float) realWidth));
            targetImageTop = (screenHeight - targetImageHeight) / 2;
        } else {
            targetImageHeight = screenHeight;
            targetImageWidth = (int) (targetImageHeight * (realWidth / (float) realHeight));
            targetImageTop = 0;
            endLeft = (screenWidth - targetImageWidth) / 2;
        }

        imageWrapper.setWidth(mOriginWidth);
        imageWrapper.setHeight(mOriginHeight);
        imageWrapper.setMarginLeft(mOriginLeft);
        imageWrapper.setMarginTop(mOriginTop);

        minWidth = (int) (targetImageWidth * DEFAULT_MIN_SCALE);
        minHeight = (int) (targetImageHeight * DEFAULT_MIN_SCALE);

        if (showImmediately) {
            mAlpha = 1f;
            backgroundView.setAlpha(mAlpha);
            min2NormalAndDrag2Min(targetImageTop, endLeft, targetImageWidth, targetImageHeight);
            isAnimating = false;
            setImageDataOfAnimatorEnd();
            changeContentViewToFullscreen();
            contentLoader.loadAnimFinish();
            if (onShowFinishListener != null) {
                onShowFinishListener.showFinish(this, true);
            }
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mOriginTop, targetImageTop);
            final int finalEndLeft = endLeft;
            valueAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                min2NormalAndDrag2Min(value, mOriginTop, targetImageTop, mOriginLeft, finalEndLeft,
                        mOriginWidth, targetImageWidth, mOriginHeight, targetImageHeight);
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                    setImageDataOfAnimatorEnd();
                    changeContentViewToFullscreen();
                    contentLoader.loadAnimFinish();
                    if (onShowFinishListener != null) {
                        onShowFinishListener.showFinish(MojitoView.this, false);
                    }

                }
            });
            valueAnimator.setDuration(animationDuration).start();
            changeBackgroundViewAlpha(false);
        }
    }

    void dragAnd2Normal(float currentY, boolean isDrag) {
        //根据触摸点的Y坐标和屏幕的比例来更改透明度
        //according to touch point y and screen ratio to change background alpha
        float alphaChangePercent = Mojito.mojitoConfig().dragMode() == IMojitoConfig.DRAG_BOTH_BOTTOM_TOP ?
                Math.abs(mMoveDownTranslateY) / screenHeight : mMoveDownTranslateY / screenHeight;
        mAlpha = 1 - alphaChangePercent;
        int originLeftOffset = (screenWidth - targetImageWidth) / 2;
        float left = 0f;
        int leftOffset = 0;
        if (Mojito.mojitoConfig().dragMode() == IMojitoConfig.DRAG_BOTH_BOTTOM_TOP) {
            float nodeMarginPercent = (MAX_Y - currentY + targetImageTop) / MAX_Y;
            if (nodeMarginPercent > 1) {
                nodeMarginPercent = 1 - (nodeMarginPercent - 1);
            }
            left = mTranslateX;
            float ratio = nodeMarginPercent;
            contentLayout.setPivotX(mDownX);
            contentLayout.setPivotY(mDownY);
            contentLayout.setScaleX(ratio);
            contentLayout.setScaleY(ratio);
        } else {
            float nodeMarginPercent = (MAX_Y - currentY + targetImageTop) / MAX_Y;
            float widthPercent = DEFAULT_MIN_SCALE + (1f - DEFAULT_MIN_SCALE) * nodeMarginPercent;
            if (nodeMarginPercent >= 1) {
                //处于拖动到正常大小上方
                imageWrapper.setWidth(targetImageWidth);
                imageWrapper.setHeight(targetImageHeight);
                left = mTranslateX;
                mAlpha = nodeMarginPercent;
            } else {
                imageWrapper.setWidth(targetImageWidth * widthPercent);
                imageWrapper.setHeight(targetImageHeight * widthPercent);
                left = mTranslateX + leftOffset;
            }
        }
        if (!isDrag) {
            left = (currentY - targetImageTop) / (releaseY - targetImageTop) * releaseLeft;
        }
        backgroundView.setAlpha(mAlpha);
        imageWrapper.setMarginLeft(Math.round(left + originLeftOffset));
        imageWrapper.setMarginTop((int) (currentY));
        contentLoader.dragging(imageWrapper.getWidth(), imageWrapper.getHeight(), imageWrapper.getWidth() / (float) screenWidth);
    }

    void min2NormalAndDrag2Min(float currentY, float startY, float endY, float startLeft, float endLeft,
                               float startWidth, float endWidth, float startHeight, float endHeight) {
        min2NormalAndDrag2Min(false, currentY, startY, endY, startLeft, endLeft, startWidth, endWidth, startHeight, endHeight);
    }

    void min2NormalAndDrag2Min(float endY, float endLeft, float endWidth, float endHeight) {
        min2NormalAndDrag2Min(true, 0, 0, endY, 0, endLeft, 0, endWidth, 0, endHeight);
    }

    void min2NormalAndDrag2Min(boolean showImmediately, float currentY, float startY, float endY, float startLeft, float endLeft,
                               float startWidth, float endWidth, float startHeight, float endHeight) {
        if (showImmediately) {
            imageWrapper.setWidth(endWidth);
            imageWrapper.setHeight(endHeight);
            imageWrapper.setMarginLeft((int) (endLeft));
            imageWrapper.setMarginTop((int) endY);
            return;
        }
        float yPercent = (currentY - startY) / (endY - startY);
        float xOffset = yPercent * (endLeft - startLeft);
        float widthOffset = yPercent * (endWidth - startWidth);
        float heightOffset = yPercent * (endHeight - startHeight);
        imageWrapper.setWidth(startWidth + widthOffset);
        imageWrapper.setHeight(startHeight + heightOffset);
        imageWrapper.setMarginLeft((int) (startLeft + xOffset));
        imageWrapper.setMarginTop((int) currentY);
    }

    void backToNormal() {
        isAnimating = true;
        releaseLeft = imageWrapper.getMarginLeft() - (screenWidth - targetImageWidth) / 2;
        releaseY = imageWrapper.getMarginTop();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(imageWrapper.getMarginTop(), targetImageTop);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            dragAnd2Normal(value, false);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });
        valueAnimator.setDuration(animationDuration).start();
        if (onReleaseListener != null) {
            onReleaseListener.onRelease(true, false);
        }
        changeBackgroundViewAlpha(false);
    }

    public void backToMin() {
        if (isAnimating) {
            return;
        }
        contentLoader.beginBackToMin(false);
        resetContentScaleParams();
        reRebuildSize();
        setReleaseParams();
        contentLoader.beginBackToMin(true);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(releaseY, mOriginTop);
        valueAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            min2NormalAndDrag2Min(value, releaseY, mOriginTop, releaseLeft, mOriginLeft, releaseWidth, mOriginWidth, releaseHeight, mOriginHeight);
        });
        valueAnimator.setDuration(animationDuration).start();
        if (onReleaseListener != null) {
            onReleaseListener.onRelease(false, true);
        }
        changeBackgroundViewAlpha(true);
    }

    private void resetContentScaleParams() {
        if (contentLayout.getScaleX() != 1) {
            Rect rectF = new Rect();
            contentLayout.getGlobalVisibleRect(rectF);

            RectF dst = new RectF(0, 0, screenWidth, screenHeight);
            contentLayout.getMatrix().mapRect(dst);
            contentLayout.setScaleX(1);
            contentLayout.setScaleY(1);

            imageWrapper.setWidth(dst.right - dst.left);
            imageWrapper.setHeight(dst.bottom - dst.top);
            imageWrapper.setMarginLeft((int) (imageWrapper.getMarginLeft() + dst.left));
            imageWrapper.setMarginTop((int) (imageWrapper.getMarginTop() + dst.top));
        }
    }

    private void reRebuildSize() {
        if (contentLoader.needReBuildSize()) {
            RectF rectF = contentLoader.getDisplayRect();
            imageLeftOfAnimatorEnd = (int) rectF.left;
            if (imageLeftOfAnimatorEnd < 0) {
                imageLeftOfAnimatorEnd = 0;
            }
            imageTopOfAnimatorEnd = (int) rectF.top;
            if (imageTopOfAnimatorEnd < 0) {
                imageTopOfAnimatorEnd = 0;
            }
            imageWidthOfAnimatorEnd = (int) rectF.right;
            if (imageWidthOfAnimatorEnd > screenWidth) {
                imageWidthOfAnimatorEnd = screenWidth;
            }
            imageHeightOfAnimatorEnd = (int) (rectF.bottom - rectF.top);
            if (imageHeightOfAnimatorEnd > screenHeight) {
                imageHeightOfAnimatorEnd = screenHeight;
            }
        }
    }


    private void setReleaseParams() {
        //到最小时,先把imageView的大小设置为imageView可见的大小,而不是包含黑色空隙部分
        // set imageView size to visible size,not include black background
        // 注意:这里 imageWrapper.getHeight() 获取的高度 是经过拖动缩放后的
        // there imageWrapper.getHeight() is scaled height
        float draggingToReleaseScale = imageWrapper.getHeight() / (float) screenHeight;
        if (imageWrapper.getHeight() != imageHeightOfAnimatorEnd) {
            releaseHeight = (int) (draggingToReleaseScale * imageHeightOfAnimatorEnd);
        } else {
            releaseHeight = imageWrapper.getHeight();
        }
        if (imageWrapper.getWidth() != imageWidthOfAnimatorEnd) {
            releaseWidth = (int) (draggingToReleaseScale * imageWidthOfAnimatorEnd);
        } else {
            releaseWidth = imageWrapper.getWidth();
        }
        if (imageWrapper.getMarginTop() != imageTopOfAnimatorEnd) {
            releaseY = imageWrapper.getMarginTop() + (int) (draggingToReleaseScale * imageTopOfAnimatorEnd);
        } else {
            releaseY = imageWrapper.getMarginTop();
        }
        if (imageWrapper.getMarginLeft() != imageLeftOfAnimatorEnd) {
            releaseLeft = imageWrapper.getMarginLeft() + (int) (draggingToReleaseScale * imageLeftOfAnimatorEnd);
        } else {
            releaseLeft = imageWrapper.getMarginLeft();
        }
        imageWrapper.setWidth(releaseWidth);
        imageWrapper.setHeight(releaseHeight);
        imageWrapper.setMarginTop((int) releaseY);
        imageWrapper.setMarginLeft(releaseLeft);
    }


    /**
     * @param isToZero 是否透明
     */
    private void changeBackgroundViewAlpha(final boolean isToZero) {
        final float end = isToZero ? 0 : 1f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mAlpha, end);
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            isAnimating = true;
            mAlpha = (Float) valueAnimator1.getAnimatedValue();
            backgroundView.setAlpha(mAlpha);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                if (isToZero) {
                    setVisibility(View.GONE);
                    if (onFinishListener != null) {
                        onFinishListener.callFinish();
                    }
                }
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                isMultiFinger = true;
                setViewPagerLocking(true);
                break;
            case MotionEvent.ACTION_DOWN:
                if (isMultiFinger) {
                    break;
                }
                mDownX = event.getX();
                mDownY = event.getY();
                mTranslateX = 0;
                mMoveDownTranslateY = 0;
                //need event when touch black background
                if (!isTouchPointInContentLayout(contentLayout, event)) {
                    mLastY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isAnimating || isMultiFinger) {
                    break;
                }
                float moveX = event.getX();
                float moveY = event.getY();
                mTranslateX = moveX - mDownX;
                mMoveDownTranslateY = moveY - mDownY;
                mYDistanceTraveled += Math.abs(mMoveDownTranslateY);

                // if touch slop too short,un need event
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mTranslateX) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    mYDistanceTraveled = 0;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                if (contentLoader.dispatchTouchEvent(isDrag, false, mMoveDownTranslateY < 0, mTranslateX > 0)) {
                    //if is long image,top or bottom or minScale, need handle event
                    //if image scale<1(origin scale) , need handle event
                    setViewPagerLocking(false);
                    break;
                }
                if (mDragListener != null) {
                    float tempTranslateY = Mojito.mojitoConfig().dragMode() == IMojitoConfig.DRAG_BOTH_BOTTOM_TOP ? Math.abs(mMoveDownTranslateY) : mMoveDownTranslateY;
                    mDragListener.onDrag(this, mTranslateX, tempTranslateY);
                }
                isDrag = true;
                int dy = y - mLastY;
                int newMarY = imageWrapper.getMarginTop() + dy;
                dragAnd2Normal(newMarY, true);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                setViewPagerLocking(false);
                break;
            case MotionEvent.ACTION_UP:
                if (isAnimating) {
                    break;
                }
                isMultiFinger = false;
                if (contentLoader.dispatchTouchEvent(isDrag, true, mMoveDownTranslateY > 0, mTranslateX > 0)) {
                    //if is long image,top or bottom or minScale, need handle event
                    //if image scale<1(origin scale) , need handle event
                    setViewPagerLocking(false);
                    break;
                }
                //如果滑动距离不足,则不需要事件
                //if touch slop too short,un need event
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mYDistanceTraveled) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }

                float tempTranslateY = Mojito.mojitoConfig().dragMode() == IMojitoConfig.DRAG_BOTH_BOTTOM_TOP ? Math.abs(mMoveDownTranslateY) : mMoveDownTranslateY;
                Log.e("tempTranslateY", "tempTranslateY:" + tempTranslateY);
                if (tempTranslateY > MAX_TRANSLATE_Y) {
                    backToMin();
                } else {
                    backToNormal();
                }
                isDrag = false;
                mYDistanceTraveled = 0;
                break;
        }

        mLastY = y;
        return super.dispatchTouchEvent(event);
    }

    private boolean isTouchPointInContentLayout(View view, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left && x <= right;
    }

    public void setContentLoader(ContentLoader view) {
        this.contentLoader = view;
        this.contentLoader.init(getContext());
        contentLayout.addView(contentLoader.providerView());
    }

    private void setViewPagerLocking(boolean lock) {
        if (onLockListener != null) {
            onLockListener.onLock(lock);
        }
    }

    private void changeContentViewToFullscreen() {
        targetImageHeight = screenHeight;
        targetImageWidth = screenWidth;
        targetImageTop = 0;
        imageWrapper.setHeight(screenHeight);
        imageWrapper.setWidth(screenWidth);
        imageWrapper.setMarginTop(0);
        imageWrapper.setMarginLeft(0);
    }

    private void setImageDataOfAnimatorEnd() {
        imageLeftOfAnimatorEnd = imageWrapper.getMarginLeft();
        imageTopOfAnimatorEnd = imageWrapper.getMarginTop();
        imageWidthOfAnimatorEnd = imageWrapper.getWidth();
        imageHeightOfAnimatorEnd = imageWrapper.getHeight();
    }


    private OnFinishListener onFinishListener;
    private OnDragListener mDragListener;
    private OnShowFinishListener onShowFinishListener;
    private OnReleaseListener onReleaseListener;
    OnLockListener onLockListener;

    public void setOnLockListener(OnLockListener onLockListener) {
        this.onLockListener = onLockListener;
    }

    public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
        this.onReleaseListener = onReleaseListener;
    }

    public void setOnShowFinishListener(OnShowFinishListener onShowFinishListener) {
        this.onShowFinishListener = onShowFinishListener;
    }

    public void setOnDragListener(OnDragListener listener) {
        mDragListener = listener;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnDragListener {
        void onDrag(MojitoView view, float moveX, float moveY);
    }

    public interface OnShowFinishListener {
        void showFinish(MojitoView mojitoView, boolean showImmediately);
    }

    public interface OnFinishListener {
        void callFinish();
    }

    public interface OnReleaseListener {
        void onRelease(boolean isToMax, boolean isToMin);
    }

    public interface OnLockListener {
        void onLock(boolean isLock);
    }

}
