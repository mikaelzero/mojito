package net.moyokoo.diooto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

import net.moyokoo.drag.R;

import java.lang.reflect.Field;

import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.zoom.ImageZoomer;

/**
 * Created by miaoyongjun.
 * Date:  2017/2/17
 */
public class DragDiootoView extends FrameLayout {
    private float mAlpha = 0;

    private float mDownX;
    private float mDownY;
    private float mTranslateY;
    private float mTranslateX;

    private final float DEFAULT_MIN_SCALE = 0.3f;
    private int MAX_TRANSLATE_Y = 0;
    private int MAX_Y = 0;

    FrameLayout contentLayout;
    View backgroundView;

    private final long DEFAULT_DURATION = 300;
    private final int DEFAULT_LONG_HEIGHT = 2000;
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
    int longImageSize = DEFAULT_LONG_HEIGHT;


    MarginViewWrapper imageWrapper;
    boolean isMulitFinger = false;
    boolean isDrag = false;
    boolean isMin2Normaling = false;
    boolean isLongHeightImage = false;
    boolean isLongWidthImage = false;
    boolean isAnimating = false;

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

    public MarginViewWrapper getImageWrapper() {
        return imageWrapper;
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
        releaseLeft = imageWrapper.getMarginLeft();
        releaseY = imageWrapper.getMarginTop();
        releaseWidth = imageWrapper.getWidth();
        releaseHeight = imageWrapper.getHeight();
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
                sketchImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(releaseY, mOriginTop);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                min2NormalAndDrag2Min(value, releaseY, mOriginTop, releaseLeft, mOriginLeft, releaseWidth, mOriginWidth, releaseHeight, mOriginHeight);
            }
        });
        valueAnimator.setDuration(animationDuration).start();
        changeBackgroundViewAlpha(true);
    }

    public void notifySize(int width, int height) {
        notifySize(width, height, false);
    }

    public void notifySize(int width, int height, boolean showRightNow) {
        realWidth = width;
        realHeight = height;

        if (isMin2Normaling) {
            return;
        }
        if (realWidth == 0 || realHeight == 0) {
            return;
        }

        int newWidth;
        int newHeight;
        if (realHeight >= longImageSize || realWidth > realHeight) {
            isLongHeightImage = realHeight >= longImageSize && getContentView() instanceof SketchImageView;
            isLongWidthImage = realWidth >= longImageSize && getContentView() instanceof SketchImageView;
            newWidth = screenWidth;
            newHeight = (int) (newWidth * (realHeight / (float) realWidth));
            if (newHeight >= screenHeight || realWidth > longImageSize) {
                newHeight = screenHeight;
            }
        } else {
            newHeight = screenHeight;
            newWidth = (int) (newHeight * (realWidth / (float) realHeight));
        }

        final int endLeft = (screenWidth - newWidth) / 2;
        final int endHeight = newHeight;
        final int endWidth = newWidth;
        if (targetImageHeight == endHeight && targetImageWidth == endWidth) {
            return;
        }
        if (isAnimating) {
            return;
        }
        if (showRightNow) {
            targetImageHeight = endHeight;
            targetImageWidth = endWidth;
            targetImageTop = (screenHeight - targetImageHeight) / 2;
            imageWrapper.setHeight(targetImageHeight);
            imageWrapper.setWidth(endWidth);
            imageWrapper.setMarginTop(targetImageTop);
            imageWrapper.setMarginLeft(endLeft);
            return;
        }
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
                targetImageHeight = endHeight;
                targetImageWidth = endWidth;
                targetImageTop = (screenHeight - targetImageHeight) / 2;
            }
        });
        animator.setDuration(animationDuration);
        animator.start();
    }

    public void putData(View originView) {
        putData(originView, 0, 0);
    }

    public void putData(View originView, int realWidth, int realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        int location[] = new int[2];
        originView.getLocationOnScreen(location);
        putData(location[0], location[1], originView.getMeasuredWidth(), originView.getMeasuredHeight());

    }

    public void putData(int left, int top, int realWidth, int realHeight) {
        mOriginLeft = left;
        mOriginTop = top;
        mOriginWidth = realWidth;
        mOriginHeight = realHeight;
    }

    public void show() {
        show(false);
    }

    public void show(boolean showImmediately) {
//        if (mOriginHeight == 0 || mOriginWidth == 0) {
//            throw new RuntimeException("you must invoke putData first");
//        }
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
            targetSize = realHeight / (float) realWidth;
        } else {
            targetSize = minViewHeight / minViewWidth;
        }
        targetImageHeight = (int) (screenWidth * targetSize);
        targetImageTop = (screenHeight - targetImageHeight) / 2;

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
            notifySize(realWidth, realHeight);
            if (onShowFinishListener != null) {
                onShowFinishListener.showFinish(this, true);
            }
        } else {
            isMin2Normaling = true;
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
                    isMin2Normaling = false;
                    notifySize(realWidth, realHeight);
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
                } else if ((float) (Math.round(sketchImageView.getZoomer().getSupportZoomScale() * 1000) / 1000) > 1) {
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

                //触摸背景需要捕捉事件
                if (!isTouchPointInContentLayout(contentLayout, event)) {
                    mLastY = y;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (isAnimating) {
                    break;
                }
                if (view instanceof SketchImageView && (isLongHeightImage || isLongWidthImage)) {
                    //长图缩放到最小比例  拖动时显示方式需要更新  并且不能启用阅读模式
                    SketchImageView sketchImageView = (SketchImageView) view;
                    if (sketchImageView.getZoomer() != null)
                        sketchImageView.getZoomer().setReadMode(false);
                    sketchImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                if (event.getPointerCount() != 1 || isMulitFinger) {
                    isMulitFinger = true;
                    break;
                }
                float moveX = event.getX();
                float moveY = event.getY();
                mTranslateX = moveX - mDownX;
                mTranslateY = moveY - mDownY;
                //如果滑动距离不足,则不需要事件
                if (Math.abs(mTranslateY) < touchSlop || (Math.abs(mTranslateX) > Math.abs(mTranslateY) && !isDrag)) {
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                if (mDragListener != null) {
                    mDragListener.onDrag(this, moveX, moveY);
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
                //如果滑动距离不足,则不需要事件
                if (Math.abs(mTranslateY) < touchSlop || (Math.abs(mTranslateX) > Math.abs(mTranslateY) && !isDrag)) {
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
                sketchImageView.getZoomer().setOnViewTapListener(new ImageZoomer.OnViewTapListener() {
                    @Override
                    public void onViewTap(@NonNull View view, float x, float y) {
                        if (onClickListener != null) onClickListener.onClick(DragDiootoView.this);
                    }
                });
            }
            sketchImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        contentLayout.addView(view);
    }

    private onFinishListener onFinishListener;
    private OnDragListener mDragListener;
    private OnShowFinishListener onShowFinishListener;
    private onClickListener onClickListener;

    public void setOnClickListener(DragDiootoView.onClickListener onClickListener) {
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

    public void setOnFinishListener(DragDiootoView.onFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface onFinishListener {
        void callFinish();
    }

    public interface onClickListener {
        void onClick(DragDiootoView dragDiootoView);
    }

    public View getContentView() {
        return contentLayout.getChildAt(0);
    }

    public ViewGroup getContentParentView() {
        return contentLayout;
    }

}
