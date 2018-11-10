package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.moyokoo.diooto.config.DiootoConfig;
import net.moyokoo.diooto.config.ContentViewOriginModel;
import net.moyokoo.diooto.interfaces.CircleIndexIndicator;
import net.moyokoo.diooto.interfaces.DefaultProgress;
import net.moyokoo.diooto.interfaces.IIndicator;
import net.moyokoo.diooto.interfaces.IProgress;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;

public class Diooto {
    Context mContext;

    public Diooto(Context context) {
        mContext = context;
        diootoConfig = new DiootoConfig();
    }

    private DiootoConfig diootoConfig;

    public Diooto urls(String imageUrl) {
        this.diootoConfig.setImageUrls(new String[]{imageUrl});
        return this;
    }

    public Diooto fullscreen(boolean isFullScreen) {
        this.diootoConfig.setFullScreen(isFullScreen);
        return this;
    }

    public Diooto urls(String[] imageUrls) {
        this.diootoConfig.setImageUrls(imageUrls);
        return this;
    }

    public Diooto type(int type) {
        this.diootoConfig.setType(type);
        return this;
    }


    public Diooto position(int position) {
        this.diootoConfig.setPosition(position);
        return this;
    }

    public Diooto views(View view) {
        View[] views = new View[1];
        views[0] = view;
        return views(views);
    }

    public Diooto views(RecyclerView recyclerView, @IdRes int viewId) {
        List<View> originImageList = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View originImage = (recyclerView.getChildAt(i)
                    .findViewById(viewId));
            originImageList.add(originImage);
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPos = 0, lastPos = 0;
        int totalCount = layoutManager.getItemCount();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayMan = (GridLayoutManager) layoutManager;
            firstPos = gridLayMan.findFirstVisibleItemPosition();
            lastPos = gridLayMan.findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linLayMan = (LinearLayoutManager) layoutManager;
            firstPos = linLayMan.findFirstVisibleItemPosition();
            lastPos = linLayMan.findLastVisibleItemPosition();
        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos);
        View[] views = new View[originImageList.size()];
        for (int i = 0; i < originImageList.size(); i++) {
            views[i] = originImageList.get(i);
        }
        return views(views);
    }

    private void fillPlaceHolder(List<View> originImageList, int totalCount, int firstPos, int lastPos) {
        if (firstPos > 0) {
            for (int pos = firstPos; pos > 0; pos--) {
                originImageList.add(0, null);
            }
        }
        if (lastPos < totalCount) {
            for (int i = (totalCount - 1 - lastPos); i > 0; i--) {
                originImageList.add(null);
            }
        }
    }

    public Diooto views(View[] views) {
        List<ContentViewOriginModel> list = new ArrayList<>();
        for (View imageView : views) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            if (imageView == null) {
                imageBean.left = 0;
                imageBean.top = 0;
                imageBean.width = 0;
                imageBean.height = 0;
            } else {
                int location[] = new int[2];
                imageView.getLocationOnScreen(location);
                imageBean.left = location[0];
                imageBean.top = location[1];
                imageBean.width = imageView.getWidth();
                imageBean.height = imageView.getHeight();
            }
            list.add(imageBean);
        }
        diootoConfig.setContentViewOriginModels(list);
        return this;
    }


    public Diooto start() {
        Window window = getWindow(mContext);
        if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            diootoConfig.setFullScreen(true);
        }
        if (!diootoConfig.isFullScreen()) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        if (ImageActivity.iIndicator == null) {
            setIndicator(new CircleIndexIndicator());
        }
        if (ImageActivity.iProgress == null) {
            setProgress(new DefaultProgress());
        }
        ImageActivity.startImageActivity((Activity) mContext, diootoConfig);
        return this;
    }

    Window getWindow(Context context) {
        if (getAppCompActivity(context) != null) {
            return getAppCompActivity(context).getWindow();
        } else {
            return scanForActivity(context).getWindow();
        }
    }

    AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public Diooto setProgress(IProgress on) {
        ImageActivity.iProgress = on;
        return this;
    }

    public Diooto setIndicator(IIndicator on) {
        ImageActivity.iIndicator = on;
        return this;
    }

    public Diooto loadPhotoBeforeShowBigImage(OnLoadPhotoBeforeShowBigImageListener on) {
        onLoadPhotoBeforeShowBigImageListener = on;
        return this;
    }

    public Diooto onVideoLoadEnd(OnShowToMaxFinishListener on) {
        onShowToMaxFinishListener = on;
        return this;
    }

    public Diooto onFinish(OnFinishListener on) {
        onFinishListener = on;
        return this;
    }

    public Diooto onProvideVideoView(OnProvideViewListener on) {
        onProvideViewListener = on;
        return this;
    }

    public static OnLoadPhotoBeforeShowBigImageListener onLoadPhotoBeforeShowBigImageListener;
    public static OnShowToMaxFinishListener onShowToMaxFinishListener;
    public static OnProvideViewListener onProvideViewListener;
    public static OnFinishListener onFinishListener;

    public interface OnLoadPhotoBeforeShowBigImageListener {
        void loadView(SketchImageView sketchImageView, int position);
    }

    public interface OnProvideViewListener {
        View provideView();
    }

    public interface OnShowToMaxFinishListener {
        void onShowToMax(DragDiootoView dragDiootoView);
    }

    public interface OnFinishListener {
        void finish(DragDiootoView dragDiootoView);
    }

    public static void cleanMemory(@NonNull Context context) {
        Sketch.with(context).getConfiguration().getDiskCache().clear();
        Sketch.with(context).getConfiguration().getBitmapPool().clear();
        Sketch.with(context).getConfiguration().getMemoryCache().clear();
    }
}
