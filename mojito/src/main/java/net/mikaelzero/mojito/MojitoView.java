package net.mikaelzero.mojito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import net.mikaelzero.mojito.interfaces.OnMojitoViewCallback;
import net.mikaelzero.mojito.loader.ContentLoader;
import net.mikaelzero.mojito.tools.MarginViewWrapper;
import net.mikaelzero.mojito.tools.ScreenUtils;
import net.mikaelzero.mojito.tools.TransitionAdapterListener;

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

    private final float MAX_TRANSLATE_Y;

    FrameLayout contentLayout;
    View backgroundView;

    long animationDuration = Mojito.mojitoConfig().duration();
    private int mOriginLeft;
    private int mOriginTop;
    private int mOriginHeight;
    private int mOriginWidth;

    private final int screenWidth;
    private final int screenHeight;
    private int targetImageTop;
    private int targetImageWidth;
    private int targetImageHeight;
    private int targetEndLeft;

    private int mLastY;

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
        screenHeight = Mojito.mojitoConfig().transparentNavigationBar() ? ScreenUtils.getScreenHeight(context) : ScreenUtils.getAppScreenHeight(context);
        MAX_TRANSLATE_Y = screenHeight * Mojito.mojitoConfig().maxTransYRatio();

        addView(LayoutInflater.from(getContext()).inflate(R.layout.layout_content, null), 0);
        contentLayout = findViewById(R.id.contentLayout);
        backgroundView = findViewById(R.id.backgroundView);
        backgroundView.setAlpha(mAlpha);
        imageWrapper = new MarginViewWrapper(contentLayout);
    }

    public void showWithoutView(int realWidth, int realHeight, boolean showImmediately) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        mOriginLeft = 0;
        mOriginTop = 0;
        mOriginWidth = 0;
        mOriginHeight = 0;

        setVisibility(View.VISIBLE);
        setOriginParams();
        min2NormalAndDrag2Min(targetImageTop, targetEndLeft, targetImageWidth, targetImageHeight);

        if (showImmediately) {
            mAlpha = 1f;
            backgroundView.setAlpha(mAlpha);
        } else {
            mAlpha = 0f;
            backgroundView.setAlpha(mAlpha);
            contentLayout.setAlpha(0f);
            contentLayout.animate().alpha(1f).setDuration(animationDuration).start();
            backgroundView.animate().alpha(1f).setDuration(animationDuration).start();
        }
        setShowEndParams();
    }

    public void putData(int left, int top, int originWidth, int originHeight, int realWidth, int realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        mOriginLeft = left;
        mOriginTop = top;
        mOriginWidth = originWidth;
        mOriginHeight = originHeight;
    }

    /**
     * 重新设置宽高  因为如果图片还未加载出来  默认宽高为全屏
     */
    public void resetSize(int w, int h) {
        if (this.realWidth == w && this.realHeight == h) {
            return;
        }
        this.realWidth = w;
        this.realHeight = h;
        setOriginParams();
        beginShow(true);
    }

    public void show(boolean showImmediately) {
        setVisibility(View.VISIBLE);
        mAlpha = showImmediately ? mAlpha = 1f : 0f;
        if (showImmediately) {
            backgroundView.setAlpha(mAlpha);
        }
        setOriginParams();
        beginShow(showImmediately);
    }

    private void setOriginParams() {
        int[] locationImage = new int[2];
        contentLayout.getLocationOnScreen(locationImage);
        targetEndLeft = 0;
        if (screenWidth / (float) screenHeight < realWidth / (float) realHeight) {
            targetImageWidth = screenWidth;
            targetImageHeight = (int) (targetImageWidth * (realHeight / (float) realWidth));
            targetImageTop = (screenHeight - targetImageHeight) / 2;
        } else {
            targetImageHeight = screenHeight;
            targetImageWidth = (int) (targetImageHeight * (realWidth / (float) realHeight));
            targetImageTop = 0;
            targetEndLeft = (screenWidth - targetImageWidth) / 2;
        }

        imageWrapper.setWidth(mOriginWidth);
        imageWrapper.setHeight(mOriginHeight);
        imageWrapper.setMarginLeft(mOriginLeft);
        imageWrapper.setMarginTop(mOriginTop);

    }

    private void beginShow(final boolean showImmediately) {
        if (showImmediately) {
            mAlpha = 1f;
            backgroundView.setAlpha(mAlpha);
            min2NormalAndDrag2Min(targetImageTop, targetEndLeft, targetImageWidth, targetImageHeight);
            setShowEndParams();
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mOriginTop, targetImageTop);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    min2NormalAndDrag2Min(value, mOriginTop, targetImageTop, mOriginLeft, targetEndLeft,
                            mOriginWidth, targetImageWidth, mOriginHeight, targetImageHeight);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setShowEndParams();
                }
            });
            valueAnimator.setDuration(animationDuration).start();
            changeBackgroundViewAlpha(false);
        }
    }

    private void setShowEndParams() {
        isAnimating = false;
        setImageDataOfAnimatorEnd();
        changeContentViewToFullscreen();
        contentLoader.loadAnimFinish();
        if (onMojitoViewCallback != null) {
            onMojitoViewCallback.showFinish(MojitoView.this, false);
        }
    }

    private void dragAnd2Normal(float currentY, boolean isDrag) {
        //根据触摸点的Y坐标和屏幕的比例来更改透明度
        //according to touch point y and screen ratio to change background alpha
        float alphaChangePercent = Math.abs(mMoveDownTranslateY) / screenHeight;
        mAlpha = 1 - alphaChangePercent;
        int originLeftOffset = (screenWidth - targetImageWidth) / 2;
        float left = 0f;
        float nodeMarginPercent = (screenHeight - currentY) / screenHeight;
        if (nodeMarginPercent > 1) {
            nodeMarginPercent = 1 - (nodeMarginPercent - 1);
        }
        left = mTranslateX;
        float ratio = nodeMarginPercent;
        contentLayout.setPivotX(mDownX);
        contentLayout.setPivotY(mDownY);
        contentLayout.setScaleX(ratio);
        contentLayout.setScaleY(ratio);
        if (!isDrag) {
            left = (currentY - targetImageTop) / (releaseY - targetImageTop) * releaseLeft;
        }
        backgroundView.setAlpha(mAlpha);
        imageWrapper.setMarginLeft(Math.round(left + originLeftOffset));
        imageWrapper.setMarginTop((int) (currentY));
        contentLoader.dragging(imageWrapper.getWidth(), imageWrapper.getHeight(), imageWrapper.getWidth() / (float) screenWidth);
    }

    private void min2NormalAndDrag2Min(float currentY, float startY, float endY, float startLeft, float endLeft,
                                       float startWidth, float endWidth, float startHeight, float endHeight) {
        min2NormalAndDrag2Min(false, currentY, startY, endY, startLeft, endLeft, startWidth, endWidth, startHeight, endHeight);
    }

    private void min2NormalAndDrag2Min(float endY, float endLeft, float endWidth, float endHeight) {
        min2NormalAndDrag2Min(true, 0, 0, endY, 0, endLeft, 0, endWidth, 0, endHeight);
    }

    private void min2NormalAndDrag2Min(boolean showImmediately, float currentY, float startY, float endY, float startLeft, float endLeft,
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

    private void backToNormal() {
        backToNormal(false);
    }

    private void backToNormal(boolean immediately) {
        contentLoader.backToNormal();
        isAnimating = !immediately;
        releaseLeft = imageWrapper.getMarginLeft() - (screenWidth - targetImageWidth) / 2;
        releaseY = imageWrapper.getMarginTop();
        if (immediately) {
            backgroundView.setAlpha(1f);
            setImageDataOfAnimatorEnd();
            changeContentViewToFullscreen();
        } else {
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
            if (onMojitoViewCallback != null) {
                onMojitoViewCallback.onRelease(true, false);
            }
            changeBackgroundViewAlpha(false);
        }
    }

    public void backToMin() {
        backToMin(false);
    }

    private void backToMin(boolean isDrag) {
        if (isAnimating) {
            return;
        }
        if (mOriginWidth == 0 || mOriginHeight == 0) {
            backToMinWithoutView();
            return;
        }
        contentLoader.beginBackToMin(false);
        if (!isDrag && contentLoader.useTransitionApi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backToMinWithTransition();
            return;
        }
        resetContentScaleParams();
        reRebuildSize();
        setReleaseParams();

        contentLoader.beginBackToMin(true);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(releaseY, mOriginTop);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                min2NormalAndDrag2Min(value, releaseY, mOriginTop, releaseLeft, mOriginLeft, releaseWidth, mOriginWidth, releaseHeight, mOriginHeight);
            }
        });
        valueAnimator.setDuration(animationDuration).start();
        if (onMojitoViewCallback != null) {
            onMojitoViewCallback.onRelease(false, true);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void backToMinWithTransition() {
        contentLayout.post(new Runnable() {
            @Override
            public void run() {
                TransitionManager.beginDelayedTransition((ViewGroup) contentLayout.getParent(), new TransitionSet()
                        .setDuration(Mojito.mojitoConfig().duration())
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeTransform())
                        .addTransition(new ChangeImageTransform())
                        .addListener(new TransitionAdapterListener() {
                            @Override
                            public void onTransitionEnd(Transition transition) {
                                if (onMojitoViewCallback != null) {
                                    onMojitoViewCallback.onMojitoViewFinish();
                                }
                            }
                        })
                );
                contentLoader.beginBackToMin(true);
                contentLayout.setTranslationX(0);
                contentLayout.setTranslationY(0);
                imageWrapper.setWidth(mOriginWidth);
                imageWrapper.setHeight(mOriginHeight);
                imageWrapper.setMarginTop(mOriginTop);
                imageWrapper.setMarginLeft(mOriginLeft);
                if (onMojitoViewCallback != null) {
                    onMojitoViewCallback.onRelease(false, true);
                }
                changeBackgroundViewAlpha(true);
            }
        });
    }


    private void backToMinWithoutView() {
        contentLayout.animate().alpha(0f).setDuration(animationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onMojitoViewCallback != null) {
                            onMojitoViewCallback.onMojitoViewFinish();
                        }
                    }
                }).start();
        backgroundView.animate().alpha(0f).setDuration(animationDuration).start();
        if (onMojitoViewCallback != null) {
            onMojitoViewCallback.onRelease(false, true);
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
            public void onAnimationUpdate(ValueAnimator animation) {
                isAnimating = true;
                mAlpha = (Float) animation.getAnimatedValue();
                backgroundView.setAlpha(mAlpha);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                if (isToZero) {
                    if (onMojitoViewCallback != null) {
                        onMojitoViewCallback.onMojitoViewFinish();
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
                if (isMultiFinger && mMoveDownTranslateY != 0) {
                    return true;
                }
                if (isAnimating || isMultiFinger) {
                    break;
                }
                float moveX = event.getX();
                float moveY = event.getY();
                mTranslateX = moveX - mDownX;
                mMoveDownTranslateY = moveY - mDownY;
                mYDistanceTraveled += Math.abs(mMoveDownTranslateY);

                // if touch slop too short,un need event
                if (Math.abs(mYDistanceTraveled) < touchSlop && (Math.abs(mTranslateX) >= Math.abs(mYDistanceTraveled) && !isDrag)) {
                    mYDistanceTraveled = 0;
                    if (isTouchPointInContentLayout(contentLayout, event)) {
                        break;
                    }
                    break;
                }
                Log.e("1", "dispatchTouchEvent: isMultiFinger" + isMultiFinger);
                if (contentLoader.dispatchTouchEvent(isDrag, false, mMoveDownTranslateY < 0, Math.abs(mTranslateX) > Math.abs(mMoveDownTranslateY))) {
                    //if is long image,top or bottom or minScale, need handle event
                    //if image scale<1(origin scale) , need handle event
                    setViewPagerLocking(false);
                    break;
                }
                handleMove(y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                setViewPagerLocking(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                //个别情况比如快速滑动或其他情况触发了ACTION_CANCEL需要重置为normal状态
                backToNormal(true);
                break;
            case MotionEvent.ACTION_UP:
                if (isAnimating) {
                    break;
                }
                isMultiFinger = false;
                if (contentLoader.dispatchTouchEvent(isDrag, true, mMoveDownTranslateY > 0, Math.abs(mTranslateX) > Math.abs(mMoveDownTranslateY))) {
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

                float tempTranslateY = Math.abs(mMoveDownTranslateY);
                if (tempTranslateY > MAX_TRANSLATE_Y) {
                    backToMin(true);
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

    public void handleMove(int y) {
        if (onMojitoViewCallback != null) {
            float tempTranslateY = Math.abs(mMoveDownTranslateY);
            onMojitoViewCallback.onDrag(this, mTranslateX, tempTranslateY);
        }
        isDrag = true;
        int dy = y - mLastY;
        int newMarY = imageWrapper.getMarginTop() + dy;
        dragAnd2Normal(newMarY, true);
    }

    //不消费该事件会导致事件交还给上级
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
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

    public void setContentLoader(ContentLoader view, String originUrl, String targetUrl) {
        this.contentLoader = view;
        this.contentLoader.init(getContext(), originUrl, targetUrl, onMojitoViewCallback);
        contentLayout.addView(contentLoader.providerView());
    }

    private void setViewPagerLocking(boolean lock) {
        if (onMojitoViewCallback != null) {
            onMojitoViewCallback.onLock(lock);
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

    private OnMojitoViewCallback onMojitoViewCallback;

    public void setOnMojitoViewCallback(OnMojitoViewCallback onMojitoViewCallback) {
        this.onMojitoViewCallback = onMojitoViewCallback;
    }

    public boolean isDrag() {
        return isDrag;
    }
}
