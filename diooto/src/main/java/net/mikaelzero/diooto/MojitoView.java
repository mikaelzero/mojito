package net.mikaelzero.diooto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ScreenUtils;

import net.mikaelzero.diooto.loader.ContentLoader;
import net.mikaelzero.diooto.tools.MarginViewWrapper;

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
    private float mXDistanceTraveled;
    private float mTranslateY;
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
    boolean isMultiFinger = false;
    boolean isDrag = false;
    boolean isAnimating = false;

    ContentLoader contentLoader;

    public MojitoView(Context context) {
        this(context, null);
    }

    public MojitoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MojitoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
        MAX_TRANSLATE_Y = screenHeight / 6;
        MAX_Y = screenHeight - screenHeight / 8;

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
            contentLoader.loadFinish();
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
                    contentLoader.loadFinish();
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
        float nodeMarginPercent = (MAX_Y - currentY + targetImageTop) / MAX_Y;
        float widthPercent = DEFAULT_MIN_SCALE + (1f - DEFAULT_MIN_SCALE) * nodeMarginPercent;
        int originLeftOffset = (screenWidth - targetImageWidth) / 2;
        int leftOffset = (int) ((targetImageWidth - targetImageWidth * widthPercent) / 2);

        float left;
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
        if (endY == startY) {
            return;
        }
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
        if (contentLoader.getCurrentScale() > 1f) {
            imageLeftOfAnimatorEnd = (int) contentLoader.getDisplayRect().left;
            imageTopOfAnimatorEnd = (int) contentLoader.getDisplayRect().top;
            imageWidthOfAnimatorEnd = (int) contentLoader.getDisplayRect().right;
            imageHeightOfAnimatorEnd = (int) (contentLoader.getDisplayRect().bottom - contentLoader.getDisplayRect().top);
        }
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

        contentLoader.beginBackToMin();

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
        if (contentLoader.dispatchTouchEvent()) {
            //如果是长图  没有缩放到最小,则不给事件
            //如果对图片进行缩放或者缩小操作 则不给事件
            return super.dispatchTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                isMultiFinger = true;
                break;
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mTranslateX = 0;
                mTranslateY = 0;
                //触摸背景需要捕捉事件
                if (!isTouchPointInContentLayout(contentLayout, event)) {
                    mLastY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                float moveX = event.getX();
                float moveY = event.getY();
                mTranslateX = moveX - mDownX;
                mTranslateY = moveY - mDownY;
                mYDistanceTraveled += Math.abs(mTranslateY);
                mXDistanceTraveled += Math.abs(mTranslateX);

                if (isAnimating) {
                    break;
                }

                if (event.getPointerCount() != 1 || isMultiFinger) {
                    isMultiFinger = true;
                    break;
                }

                //如果滑动距离不足,则不需要事件
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mTranslateX) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    mYDistanceTraveled = 0;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                if (mDragListener != null) {
                    mDragListener.onDrag(this, mTranslateX, mTranslateY);
                }
                isDrag = true;
                int dy = y - mLastY;
                int newMarY = imageWrapper.getMarginTop() + dy;

                //根据触摸点的Y坐标和屏幕的比例来更改透明度
                float alphaChangePercent = mTranslateY / screenHeight;
                mAlpha = 1 - alphaChangePercent;
                dragAnd2Normal(newMarY, true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (isAnimating) {
                    break;
                }
                //如果滑动距离不足,则不需要事件
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mYDistanceTraveled) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    if (!isMultiFinger && onClickListener != null) {
                        onClickListener.onClick(MojitoView.this);
                    }
                    isMultiFinger = false;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                //防止滑动时出现多点触控
                if (isMultiFinger && !isDrag) {
                    isMultiFinger = false;
                    break;
                }
                isMultiFinger = false;
                if (mTranslateY > MAX_TRANSLATE_Y) {
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
        return y >= top && y <= bottom && x >= left
                && x <= right;
    }

    public void setContentLoader(ContentLoader view) {
        this.contentLoader = view;
        this.contentLoader.init(getContext());
//        this.contentView.providerView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                backToMin();
//            }
//        });
        contentLayout.addView(contentLoader.providerView());
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
    private OnClickListener onClickListener;
    private OnReleaseListener onReleaseListener;

    public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
        this.onReleaseListener = onReleaseListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnShowFinishListener(OnShowFinishListener onShowFinishListener) {
        this.onShowFinishListener = onShowFinishListener;
    }

    public void setOnDragListener(OnDragListener listener) {
        mDragListener = listener;
    }

    public interface OnDragListener {
        void onDrag(MojitoView view, float moveX, float moveY);
    }

    public interface OnShowFinishListener {
        void showFinish(MojitoView mojitoView, boolean showImmediately);
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void callFinish();
    }

    public interface OnReleaseListener {
        void onRelease(boolean isToMax, boolean isToMin);
    }

    public interface OnClickListener {
        void onClick(MojitoView mojitoView);
    }
}
