package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import net.moyokoo.drag.R;

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

public class DragDiooto2 {

    public DragDiooto2(Context context) {
        mContext = context;
    }

    public static int PHOTO = 1;
    public static int VIDEO = 2;
    private ImageFragment[] mDragDiootoViews;
    private DragDiootoView singleView;

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
    public DragDiooto2 fullscreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        return this;
    }

    public DragDiooto2 loadPhotoBeforeShowBigImage(OnLoadPhotoBeforeShowBigImage onLoadPhotoBeforeShowBigImage) {
        this.onLoadPhotoBeforeShowBigImage = onLoadPhotoBeforeShowBigImage;
        return this;
    }

    public DragDiooto2 urls(String imageUrl) {
        this.imageUrls = new String[1];
        this.imageUrls[0] = imageUrl;
        return this;
    }

    public DragDiooto2 urls(String[] imageUrls) {
        this.imageUrls = imageUrls;
        return this;
    }

    public DragDiooto2 type(int type) {
        this.contentType = type;
        return this;
    }


    public DragDiooto2 position(int position) {
        initPosition = position;
        return this;
    }

    public DragDiooto2 size(int[] realWidth, int[] realHeight) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        return this;
    }

    /**
     * 只加载一个View的宽高
     */
    public DragDiooto2 size(int realWidth, int realHeight) {
        this.realWidth = new int[1];
        this.realHeight = new int[1];
        this.realWidth[0] = realWidth;
        this.realHeight[0] = realHeight;
        return this;
    }

    public DragDiooto2 views(View[] views) {
        this.views = views;
        return this;
    }

    /**
     * 只加载一个View  单张图片或者视频
     */
    public DragDiooto2 views(View views) {
        this.views = new View[1];
        this.views[0] = views;
        return this;
    }


    /**
     * 视频不支持此模式
     */
    private void initMultipleView(FragmentManager fm) {
        mViewPager = hostLayout.getViewPager();
        final List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ImageFragment imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", imageUrls[i]);
            imageFragment.setArguments(bundle);
            fragmentList.add(imageFragment);
        }
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        mViewPager.setVisibility(View.VISIBLE);
    }


    public DragDiooto2 start(FragmentManager fm) {
        ImageActivity.startImageActivity((Activity) mContext, views, imageUrls, initPosition);
        return this;
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


    private void hideStatus(Context context) {
        if (isFullScreen) {
            return;
        }
        hostLayout = StatusUtil.with((FragmentActivity) mContext, isFullScreen).hideStatus(context).type(contentType);
    }

    HostLayout hostLayout;

    private void showStatus(Context context) {
        if (isFullScreen) {
            return;
        }
        StatusUtil.with((FragmentActivity) mContext, isFullScreen).showStatus(context);
    }

    public void e(String msg) {
        Log.e("1", msg);
    }
}
