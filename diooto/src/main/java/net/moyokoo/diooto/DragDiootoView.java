package net.moyokoo.diooto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.moyokoo.dio.R;

import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.decode.ImageSizeCalculator;

/**
 * Created by moyokoo.
 * Date:  2017/2/17
 */
public class DragDiootoView extends FrameLayout {
    private float mAlpha = 0;
    private float mDownX;
    private float mDownY;
    private float mYDistanceTraveled;
    private float mXDistanceTraveled;
    private float mTranslateY;
    private float mTranslateX;

    private final float DEFAULT_MIN_SCALE = 0.3f;
    private int MAX_TRANSLATE_Y = 0;
    private int MAX_Y = 0;

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
    boolean isMulitFinger = false;
    boolean isDrag = false;
    boolean isLongHeightImage = false;//是否是高度长图
    boolean isLongWidthImage = false;//是否是宽度长图
    boolean isAnimating = false;//是否在动画中
    boolean isPhoto = false;
    boolean mIsLongPressed = false;
    boolean longClickable = false;

    public DragDiootoView(Context context) {
        this(context, null);
    }

    public DragDiootoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDiootoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        MAX_TRANSLATE_Y = screenHeight / 6;
        MAX_Y = screenHeight - screenHeight / 8;

        addView(LayoutInflater.from(getContext()).inflate(R.layout.content_item, null), 0);
        contentLayout = findViewById(R.id.contentLayout);
        backgroundView = findViewById(R.id.backgroundView);
        imageWrapper = new MarginViewWrapper(contentLayout);
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
    }

    void backToNormal() {

        isAnimating = true;
        releaseLeft = imageWrapper.getMarginLeft() - (screenWidth - targetImageWidth) / 2;
        releaseY = imageWrapper.getMarginTop();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(imageWrapper.getMarginTop(), targetImageTop);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                dragAnd2Normal(value, false);
            }
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

    public void backToMin() {
        if (isAnimating) {
            return;
        }
        //到最小时,先把imageView的大小设置为imageView可见的大小,而不是包含黑色空隙部分
        if (isPhoto) {
            // 注意:这里 imageWrapper.getHeight() 获取的高度 是经过拖动缩放后的
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
        } else {
            releaseLeft = imageWrapper.getMarginLeft();
            releaseY = imageWrapper.getMarginTop();
            releaseWidth = imageWrapper.getWidth();
            releaseHeight = imageWrapper.getHeight();
        }

        if ((isLongHeightImage || isLongWidthImage) && getContentView() instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) getContentView();
            if (sketchImageView.getZoomer() != null) {
                //如果是长图 则重新更改宽高 因为长图缩放到最小时需要大小变化
                float ratio = sketchImageView.getZoomer().getZoomScale() / sketchImageView.getZoomer().getMaxZoomScale();
                if (isLongHeightImage) {
                    int tempWidth = (int) (screenWidth * ratio);
                    releaseLeft = releaseLeft + (releaseWidth - tempWidth) / 2;
                    releaseWidth = tempWidth;
                } else {
                    int tempHeight = (int) (screenHeight * ratio);
                    releaseY = releaseY + (releaseHeight - tempHeight) / 2;
                    releaseHeight = tempHeight;
                }
                changeImageViewToCenterCrop();
            }
        }
        changeImageViewToCenterCrop();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(releaseY, mOriginTop);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                min2NormalAndDrag2Min(value, releaseY, mOriginTop, releaseLeft, mOriginLeft, releaseWidth, mOriginWidth, releaseHeight, mOriginHeight);
            }
        });
        valueAnimator.setDuration(animationDuration).start();
        if (onReleaseListener != null) {
            onReleaseListener.onRelease(false, true);
        }
        changeBackgroundViewAlpha(true);
    }


    /**
     * 配置更改后 重新更新大小  根据存储的原宽高进行更新
     */
    public void notifySizeConfig() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        notifySize(realWidth, realHeight);
    }

    public void notifySize(int width, int height) {
        notifySize(width, height, false);
    }

    public void notifySize(int width, int height, boolean showRightNow) {
        realWidth = width;
        realHeight = height;


        if (realWidth == 0 || realHeight == 0) {
            return;
        }

        int newWidth;
        int newHeight;
        ImageSizeCalculator sizeCalculator = new ImageSizeCalculator();
        if (sizeCalculator.canUseReadModeByHeight(realWidth, realHeight) ||
                sizeCalculator.canUseReadModeByWidth(realWidth, realHeight) ||
                screenWidth / (float) screenHeight < realWidth / (float) realHeight
                ) {
            isLongHeightImage = sizeCalculator.canUseReadModeByHeight(realWidth, realHeight) && getContentView() instanceof SketchImageView;
            isLongWidthImage = sizeCalculator.canUseReadModeByWidth(realWidth, realHeight) && getContentView() instanceof SketchImageView;
            newWidth = screenWidth;
            newHeight = (int) (newWidth * (realHeight / (float) realWidth));
            if (newHeight >= screenHeight || sizeCalculator.canUseReadModeByWidth(realWidth, realHeight)) {
                newHeight = screenHeight;
            }
        } else {
            newHeight = screenHeight;
            newWidth = (int) (newHeight * (realWidth / (float) realHeight));
        }

        final int endLeft = (screenWidth - newWidth) / 2;
        final int endHeight = newHeight;
        final int endWidth = newWidth;
//        if (targetImageHeight == endHeight && targetImageWidth == endWidth) {
//            return;
//        }

        if (showRightNow) {
            targetImageHeight = endHeight;
            targetImageWidth = endWidth;
            targetImageTop = (screenHeight - targetImageHeight) / 2;
            imageWrapper.setHeight(targetImageHeight);
            imageWrapper.setWidth(endWidth);
            imageWrapper.setMarginTop(targetImageTop);
            imageWrapper.setMarginLeft(endLeft);
            if (isPhoto) {
                setImageDataOfAnimatorEnd();
                changeContentViewToFullscreen();
            }
            return;
        }

        //如果Y轴不进行变化  则只变化宽高 该情况暂时发现用在屏幕旋转的情况下 正方形图片
        if (targetImageTop == (screenHeight - endHeight) / 2) {
            final int startLeft = imageWrapper.getMarginLeft();
            ValueAnimator animator = ValueAnimator.ofInt(targetImageWidth, endWidth);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    isAnimating = true;
                    int value = (int) valueAnimator.getAnimatedValue();
                    if (endWidth - targetImageWidth == 0) {
                        imageWrapper.setWidth(targetImageWidth);
                        imageWrapper.setHeight(targetImageHeight);
                        imageWrapper.setMarginLeft((int) (startLeft));
                    } else {
                        float yPercent = (value - targetImageWidth) / (float) (endWidth - targetImageWidth);
                        float xOffset = yPercent * (endLeft - startLeft);
                        float widthOffset = yPercent * (endWidth - targetImageWidth);
                        float heightOffset = yPercent * (endHeight - targetImageHeight);
                        imageWrapper.setWidth(targetImageWidth + widthOffset);
                        imageWrapper.setHeight(targetImageHeight + heightOffset);
                        imageWrapper.setMarginLeft((int) (startLeft + xOffset));
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                    setImageDataOfAnimatorEnd();
                    if (isPhoto) {
                        changeContentViewToFullscreen();
                    } else {
                        targetImageHeight = endHeight;
                        targetImageWidth = endWidth;
                        targetImageTop = (screenHeight - targetImageHeight) / 2;
                    }
                }
            });
            animator.setDuration(animationDuration);
            animator.start();
        } else {
            ValueAnimator animator = ValueAnimator.ofInt(targetImageTop, (screenHeight - endHeight) / 2);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    isAnimating = true;
                    int value = (int) valueAnimator.getAnimatedValue();
                    min2NormalAndDrag2Min(value, targetImageTop, (screenHeight - endHeight) / 2,
                            0, endLeft, targetImageWidth, endWidth,
                            targetImageHeight, endHeight);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                    setImageDataOfAnimatorEnd();
                    if (isPhoto) {
                        changeContentViewToFullscreen();
                    } else {
                        targetImageHeight = endHeight;
                        targetImageWidth = endWidth;
                        targetImageTop = (screenHeight - targetImageHeight) / 2;
                    }
                }
            });
            animator.setDuration(animationDuration);
            animator.start();
        }
    }


    public void putData(int left, int top, int originWidth, int originHeight) {
        putData(left, top, originWidth, originHeight, 0, 0);
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
        setVisibility(View.VISIBLE);
        mAlpha = showImmediately ? mAlpha = 1 : 0;
        getLocation(mOriginWidth, mOriginHeight, showImmediately);
    }


    private void getLocation(float minViewWidth, float minViewHeight, final boolean showImmediately) {
        int[] locationImage = new int[2];
        contentLayout.getLocationOnScreen(locationImage);
        float targetSize;
        targetImageWidth = screenWidth;
        if (realHeight != 0 && realWidth != 0) {
            notifySize(realWidth, realHeight, true);
            return;
        } else {
            targetSize = minViewHeight / minViewWidth;
            targetImageHeight = (int) (screenWidth * targetSize);
            targetImageTop = (screenHeight - targetImageHeight) / 2;
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
            min2NormalAndDrag2Min(targetImageTop, 0, targetImageWidth, targetImageHeight);
            if (onShowFinishListener != null) {
                onShowFinishListener.showFinish(this, true);
            }
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mOriginTop, targetImageTop);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    min2NormalAndDrag2Min(value, mOriginTop, targetImageTop, mOriginLeft, 0,
                            mOriginWidth, targetImageWidth, mOriginHeight, targetImageHeight);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                    if (onShowFinishListener != null) {
                        onShowFinishListener.showFinish(DragDiootoView.this, false);
                    }
                }
            });
            valueAnimator.setDuration(animationDuration).start();
            changeBackgroundViewAlpha(false);
        }
    }

    /**
     * @param isToZero 是否透明
     */
    private void changeBackgroundViewAlpha(final boolean isToZero) {
        final float end = isToZero ? 0 : 1f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mAlpha, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                isAnimating = true;
                mAlpha = (Float) valueAnimator.getAnimatedValue();
                backgroundView.setAlpha(mAlpha);
            }
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
        View view = getContentView();
        if (view instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) view;
            //如果是长图  没有缩放到最小,则不给事件
            if (sketchImageView.getZoomer() != null) {
                if (isLongHeightImage || isLongWidthImage) {
                    if (sketchImageView.getZoomer().getZoomScale() > sketchImageView.getZoomer().getMinZoomScale()) {
                        return super.dispatchTouchEvent(event);
                    }
                } else if ((Math.round(sketchImageView.getZoomer().getSupportZoomScale() * 1000) / 1000f) > 1) {
                    //如果对图片进行缩放或者缩小操作 则不给事件
                    return super.dispatchTouchEvent(event);
                }
            }
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                isMulitFinger = true;
                break;
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mTranslateX = 0;
                mTranslateY = 0;
                if (longClickable) {
                    postDelayed(mLongPressedRunable, ViewConfiguration.getLongPressTimeout());
                }
                //触摸背景需要捕捉事件
                if (!isTouchPointInContentLayout(contentLayout, event)) {
                    mLastY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                mTranslateX = moveX - mDownX;
                mTranslateY = moveY - mDownY;
                mYDistanceTraveled += Math.abs(mTranslateY);
                mXDistanceTraveled += Math.abs(mTranslateX);

                if (isAnimating) {
                    break;
                }

                if (view instanceof SketchImageView && (isLongHeightImage || isLongWidthImage)) {
                    //长图缩放到最小比例  拖动时显示方式需要更新  并且不能启用阅读模式
                    SketchImageView sketchImageView = (SketchImageView) view;
                    if (sketchImageView.getZoomer() != null) {
                        sketchImageView.getZoomer().setReadMode(false);
                    }
                    changeImageViewToFitCenter();
                }
                if (event.getPointerCount() != 1 || isMulitFinger) {
                    isMulitFinger = true;
                    break;
                }

                //如果X移动超过最小距离  则不给长按事件
                if (Math.abs(mXDistanceTraveled) > touchSlop || (Math.abs(mTranslateX) < Math.abs(mXDistanceTraveled))) {
                    mIsLongPressed = false;
                }
                //如果滑动距离不足,则不需要事件
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mTranslateX) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    mYDistanceTraveled = 0;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }

                mIsLongPressed = false;
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
                if (isAnimating) {
                    break;
                }
                removeCallbacks(mLongPressedRunable);
                //如果滑动距离不足,则不需要事件
                if (Math.abs(mYDistanceTraveled) < touchSlop || (Math.abs(mYDistanceTraveled) > Math.abs(mYDistanceTraveled) && !isDrag)) {
                    if (!isMulitFinger && onClickListener != null) {
                        onClickListener.onClick(DragDiootoView.this);
                    }
                    isMulitFinger = false;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                //防止滑动时出现多点触控
                if (isMulitFinger && !isDrag) {
                    isMulitFinger = false;
                    break;
                }
                isMulitFinger = false;
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

    Runnable mLongPressedRunable = new Runnable() {
        public void run() {
            if (longClickable && mIsLongPressed) {
                if (onDiootoLongClickListener != null) {
                    onDiootoLongClickListener.longClick();
                }
            }else{
                mIsLongPressed = longClickable;
            }
        }
    };


    public class MarginViewWrapper {
        private MarginLayoutParams params;
        private View viewWrapper;

        MarginViewWrapper(View view) {
            this.viewWrapper = view;
            params = (MarginLayoutParams) viewWrapper.getLayoutParams();
            if (params instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) params).gravity = Gravity.START;
            }
        }

        public int getWidth() {
            return params.width;
        }

        public int getHeight() {
            return params.height;
        }

        void setWidth(float width) {
            params.width = Math.round(width);
            viewWrapper.setLayoutParams(params);
        }

        void setHeight(float height) {
            params.height = Math.round(height);
            viewWrapper.setLayoutParams(params);
        }

        void setMarginTop(int m) {
            params.topMargin = m;
            viewWrapper.setLayoutParams(params);
        }

        void setMarginBottom(int m) {
            params.bottomMargin = m;
            viewWrapper.setLayoutParams(params);
        }

        public int getMarginTop() {
            return params.topMargin;
        }

        void setMarginRight(int mr) {
            params.rightMargin = mr;
            viewWrapper.setLayoutParams(params);
        }

        void setMarginLeft(int mr) {
            params.leftMargin = mr;
            viewWrapper.setLayoutParams(params);
        }

        int getMarginRight() {
            return params.rightMargin;
        }

        public int getMarginLeft() {
            return params.leftMargin;
        }

        int getMarginBottom() {
            return params.bottomMargin;
        }
    }

    public void addContentChildView(View view) {
        ViewGroup parentViewGroup = (ViewGroup) view.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(view);
        }
        if (view instanceof SketchImageView) {
            SketchImageView sketchImageView = (SketchImageView) view;
            if (sketchImageView.getZoomer() != null) {
                sketchImageView.getZoomer().setReadMode(true);
                sketchImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backToMin();
                    }
                });
            }
            sketchImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        contentLayout.addView(view);
    }

    private void changeContentViewToFullscreen() {
        targetImageHeight = screenHeight;
        targetImageWidth = screenWidth;
        targetImageTop = 0;
        changeImageViewToFitCenter();
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

    private void changeImageViewToFitCenter() {
        if (getContentView() instanceof SketchImageView) {
            ((SketchImageView) getContentView()).setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    private void changeImageViewToCenterCrop() {
        if (getContentView() instanceof SketchImageView) {
            ((SketchImageView) getContentView()).setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    private OnFinishListener onFinishListener;
    private OnDragListener mDragListener;
    private OnShowFinishListener onShowFinishListener;
    private OnClickListener onClickListener;
    private OnReleaseListener onReleaseListener;
    private OnDiootoLongClickListener onDiootoLongClickListener;

    public void setOnDiootoLongClickListener(OnDiootoLongClickListener onDiootoLongClickListener) {
        this.onDiootoLongClickListener = onDiootoLongClickListener;
        longClickable = true;
        mIsLongPressed = true;
    }

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
        void onDrag(DragDiootoView view, float moveX, float moveY);
    }

    public interface OnShowFinishListener {
        void showFinish(DragDiootoView dragDiootoView, boolean showImmediately);
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
        void onClick(DragDiootoView dragDiootoView);
    }

    public interface OnDiootoLongClickListener {
        void longClick();
    }

    //获得可滑动view的布局中添加的子view
    public View getContentView() {
        return contentLayout.getChildAt(0);
    }

    public ViewGroup getContentParentView() {
        return contentLayout;
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public void setPhoto(boolean photo) {
        isPhoto = photo;
    }
}
