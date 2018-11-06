package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DownloadProgressListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.request.LoadResult;
import me.panpf.sketch.util.SketchUtils;

public class DragDiooto {

    public DragDiooto(Context context) {
        mContext = context;
    }

    public static int PHOTO = 1;
    public static int VIDEO = 2;
    private DragDiootoView[] mDragDiootoViews;
    private DragDiootoView singleView;
    private LoadingView[] loadingViews;

    private String[] imageUrls;

    private Context mContext;

    private int contentType = PHOTO;
    private ViewPager mViewPager;
    private OnLoadPhotoBeforeShowBigImage onLoadPhotoBeforeShowBigImage;
    private OnShowToMaxFinish onShowToMaxFinish;
    private OnProvideVideoView onProvideVideoView;
    private OnFinish onFinish;
    private int initPosition = 0;
    private View[] views;
    private int[] realWidth;
    private int[] realHeight;
    private boolean isFullScreen = false;
    //后续考虑是否加入缓存
    private List<Integer> loadPositions = new ArrayList<>();
    private final Object key = new Object();

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public int getCurrentPosition() {
        return mViewPager.getCurrentItem();
    }

    //所在的Activity是否为全屏 默认false
    public DragDiooto fullscreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        return this;
    }

    public DragDiooto loadPhotoBeforeShowBigImage(OnLoadPhotoBeforeShowBigImage onLoadPhotoBeforeShowBigImage) {
        this.onLoadPhotoBeforeShowBigImage = onLoadPhotoBeforeShowBigImage;
        return this;
    }

    public DragDiooto urls(String imageUrl) {
        this.imageUrls = new String[1];
        this.imageUrls[0] = imageUrl;
        return this;
    }

    public DragDiooto urls(String[] imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public DragDiooto type(int type) {
        this.contentType = type;
        return this;
    }

    public DragDiootoView getDrPhotoDio(int position) {
        return mDragDiootoViews[position];
    }

    public SketchImageView getSketchImageView(int position) {
        return (SketchImageView) mDragDiootoViews[position].getContentView();
    }

    public DragDiooto onVideoLoadEnd(OnShowToMaxFinish onShowToMaxFinish) {
        this.onShowToMaxFinish = onShowToMaxFinish;
        return this;
    }

    public DragDiooto onFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public DragDiooto onProvideVideoView(OnProvideVideoView onProvideVideoView) {
        this.onProvideVideoView = onProvideVideoView;
        return this;
    }

    public DragDiooto position(int position) {
        initPosition = position;
        return this;
    }

    public DragDiooto size(int[] realWidth, int[] realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        return this;
    }

    /**
     * 只加载一个View的宽高
     */
    public DragDiooto size(int realWidth, int realHeight) {
        this.realWidth = new int[1];
        this.realHeight = new int[1];
        this.realWidth[0] = realWidth;
        this.realHeight[0] = realHeight;
        return this;
    }

    public DragDiooto views(View[] views) {
        this.views = views;
        return this;
    }

    /**
     * 只加载一个View  单张图片或者视频
     */
    public DragDiooto views(View views) {
        this.views = new View[1];
        this.views[0] = views;
        return this;
    }

    public DragDiootoView getCurrentDrPhotoDioView() {
        return mDragDiootoViews[getCurrentPosition()];
    }

    /**
     * 视频不支持此模式
     */
    private void initMultipleView() {
        mDragDiootoViews = new DragDiootoView[views.length];
        loadingViews = new LoadingView[views.length];
        mViewPager = StatusUtil.with((Activity) mContext, isFullScreen).getViewPager();
        for (int i = 0; i < mDragDiootoViews.length; i++) {
            final int position = i;
            mDragDiootoViews[i] = new DragDiootoView(mContext);
            loadingViews[i] = new LoadingView(mContext);
            int size = Utils.dip2px(mContext, 60);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.gravity = Gravity.CENTER;
            loadingViews[i].setLayoutParams(params);
            loadingViews[i].setVisibility(View.GONE);
            if (contentType == PHOTO) {
                SketchImageView photoView = new SketchImageView(mContext);
                photoView.getOptions().setDecodeGifImage(true);
                photoView.setZoomEnabled(true);
                mDragDiootoViews[i].addContentChildView(photoView);
            }
            mDragDiootoViews[i].addContentChildView(loadingViews[i]);
            mDragDiootoViews[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mDragDiootoViews[i].setOnFinishListener(new DragDiootoView.onFinishListener() {
                @Override
                public void callFinish() {
                    mViewPager.setVisibility(View.GONE);
                    showStatus(mContext);
                }
            });
            mDragDiootoViews[i].setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
                @Override
                public void showFinish(DragDiootoView view, boolean showImmediately) {
                    if (contentType == PHOTO && view.getContentView() instanceof SketchImageView && position == initPosition) {
                        loadImage(position);
                    }
                }
            });
            mDragDiootoViews[i].setOnClickListener(new DragDiootoView.onClickListener() {
                @Override
                public void onClick(DragDiootoView dragDiootoView) {
                    if (mViewPager != null && mViewPager.getVisibility() == View.VISIBLE) {
                        getCurrentDrPhotoDioView().backToMin();
                    } else if (singleView != null && singleView.getVisibility() == View.VISIBLE) {
                        singleView.backToMin();
                    }
                }
            });
        }
        mViewPager.setOffscreenPageLimit(views.length);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                container.addView(mDragDiootoViews[position]);
                return mDragDiootoViews[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mDragDiootoViews[position]);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                startPositionBlockDisplayer(position);
                if (position != initPosition && contentType == PHOTO && getDrPhotoDio(position).getContentView() instanceof SketchImageView) {
                    loadImage(position);
                }
            }
        });
        mViewPager.setCurrentItem(initPosition);
        mViewPager.setVisibility(View.VISIBLE);
        for (int i = 0; i < views.length; i++) {
            if (onLoadPhotoBeforeShowBigImage != null && mDragDiootoViews[i].getContentView() instanceof SketchImageView) {
                onLoadPhotoBeforeShowBigImage.loadView((SketchImageView) mDragDiootoViews[i].getContentView(), i);
            }
            if (realWidth == null || realHeight == null) {
                mDragDiootoViews[i].putData(views[i]);
            } else {
                mDragDiootoViews[i].putData(views[i], realWidth[i], realHeight[i]);
            }
            if (i != initPosition) {
                mDragDiootoViews[i].show(true);
            }
        }
        mDragDiootoViews[initPosition].show();
    }

    private void initSingleView() {
        singleView = StatusUtil.with((Activity) mContext, isFullScreen).getSingleView();
        if (contentType == VIDEO) {
            if (onProvideVideoView == null) {
                throw new RuntimeException("you should set onProvideVideoView first if you use VIDEO");
            }
            if (singleView.getContentParentView().getChildCount() <= 0) {
                singleView.addContentChildView(onProvideVideoView.provideView());
                SketchImageView photoView = new SketchImageView(mContext);
                singleView.addContentChildView(photoView);
            }
        }
        if (onLoadPhotoBeforeShowBigImage != null && singleView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
            onLoadPhotoBeforeShowBigImage.loadView((SketchImageView) singleView.getContentParentView().getChildAt(1), 0);
            singleView.getContentParentView().getChildAt(1).setVisibility(View.VISIBLE);
        }
        if (realWidth == null || realHeight == null) {
            singleView.putData(views[0]);
        } else {
            singleView.putData(views[0], realWidth[0], realHeight[0]);
        }
        singleView.show();
        singleView.setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
            @Override
            public void showFinish(DragDiootoView dragDiootoView, boolean showImmediately) {
                if (singleView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                    singleView.getContentParentView().getChildAt(1).setVisibility(View.GONE);
                }
                onShowToMaxFinish.onShowToMax(dragDiootoView);

            }
        });
        singleView.setOnFinishListener(new DragDiootoView.onFinishListener() {
            @Override
            public void callFinish() {
                singleView.setVisibility(View.GONE);
                showStatus(mContext);
                onFinish.finish(singleView);
            }
        });
        if (contentType == PHOTO && singleView.getContentView() instanceof SketchImageView) {
            singleView.setOnClickListener(new DragDiootoView.onClickListener() {
                @Override
                public void onClick(DragDiootoView dragDiootoView) {
                    if (mViewPager != null && mViewPager.getVisibility() == View.VISIBLE) {
                        getCurrentDrPhotoDioView().backToMin();
                    } else if (singleView != null && singleView.getVisibility() == View.VISIBLE) {
                        singleView.backToMin();
                    }
                }
            });
        }
    }

    public DragDiooto start() {
        verification();
        if ((((Activity) (mContext)).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            isFullScreen = true;
        }
        hideStatus(mContext);
        if (contentType == PHOTO) {
            initMultipleView();
        } else {
            initSingleView();
        }
        return this;
    }

    private void startPositionBlockDisplayer(int position) {
        for (int i = 0; i < mDragDiootoViews.length; i++) {
            if (isSketchImageView(position)) {
                SketchImageView sketchImageView = getSketchImageView(position);
                if (sketchImageView.getZoomer() != null && sketchImageView.isZoomEnabled()) {
                    //TODO BlockDisplayer的处理
                    e("isWork:" + sketchImageView.getZoomer().getBlockDisplayer().isReady() + "    position:" + i);
                    sketchImageView.getZoomer().getBlockDisplayer().setPause(false);
                    e("isWork2:" + sketchImageView.getZoomer().getBlockDisplayer().isReady() + "    position:" + i);
                }
                Drawable drawable = SketchUtils.getLastDrawable(sketchImageView.getDrawable());
                if (drawable != null && drawable instanceof SketchGifDrawable) {
                    ((SketchGifDrawable) drawable).followPageVisible(position != i, true);
                }
            }
        }
    }

    /**
     * 加载当前位置以及两边
     */
    private void loadImage(final int position) {
        if (loadPositions.contains(position)) {
            return;
        }
        if (position - 1 != -1) {
            synchronized (key) {
                loadPositions.add(position - 1);
            }
            loadImage((SketchImageView) getDrPhotoDio(position - 1).getContentView(), position - 1);
        }
        synchronized (key) {
            loadPositions.add(position);
        }
        loadImage((SketchImageView) getDrPhotoDio(position).getContentView(), position);
        if (position + 1 != views.length) {
            synchronized (key) {
                loadPositions.add(position + 1);
            }
            loadImage((SketchImageView) getDrPhotoDio(position + 1).getContentView(), position + 1);
        }
    }

    private boolean isSketchImageView(int position) {
        return mDragDiootoViews[position].getContentView() instanceof SketchImageView;
    }

    private void loadImage(final SketchImageView sketchImageView, final int position) {
        Sketch.with(mContext).load(imageUrls[position], new LoadListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onCompleted(@NonNull LoadResult result) {
                if (result.getGifDrawable() != null) {
                    result.getGifDrawable().followPageVisible(true, true);
                }
                loadingViews[position].setVisibility(View.GONE);
                int w = result.getBitmap().getWidth();
                int h = result.getBitmap().getHeight();
                mDragDiootoViews[position].notifySize(w, h);
                sketchImageView.displayImage(imageUrls[position]);
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                synchronized (key) {
                    loadPositions.remove(position);
                }
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
                synchronized (key) {
                    loadPositions.remove(position);
                }
            }
        }).downloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                int ratio = (int) (completedLength / (float) totalLength * 100);
                loadingViews[position].setVisibility(View.VISIBLE);
                loadingViews[position].setProgress(ratio);
            }
        }).commit();
    }

    public interface OnLoadPhotoBeforeShowBigImage {
        void loadView(SketchImageView sketchImageView, int position);
    }

    public interface OnProvideVideoView {
        View provideView();
    }

    public interface OnShowToMaxFinish {
        void onShowToMax(DragDiootoView dragDiootoView);
    }

    public interface OnFinish {
        void finish(DragDiootoView dragDiootoView);
    }


    private void verification() {
        if (mContext == null) {
            throw new RuntimeException("Context can not be  null");
        }
        if (views == null || views.length == 0) {
            throw new RuntimeException("You must set widths heights views first");
        }
        if (imageUrls == null || imageUrls.length == 0) {
            throw new RuntimeException("You must set imageUrls first");
        }
    }

    public boolean handleKeyDown(int keyCode) {
        if (mViewPager != null && mViewPager.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
            getCurrentDrPhotoDioView().backToMin();
            return true;
        } else if (singleView != null && singleView.getVisibility() == View.VISIBLE && keyCode == KeyEvent.KEYCODE_BACK) {
            singleView.backToMin();
            return true;
        }
        return false;
    }


    private void hideStatus(Context context) {
        if (isFullScreen) {
            return;
        }
        StatusUtil.with((Activity) mContext, isFullScreen).hideStatus(context).type(contentType);
    }

    private void showStatus(Context context) {
        if (isFullScreen) {
            return;
        }
        StatusUtil.with((Activity) mContext, isFullScreen).showStatus(context);
    }

    public void e(String msg) {
        Log.e("1", msg);
    }
}
