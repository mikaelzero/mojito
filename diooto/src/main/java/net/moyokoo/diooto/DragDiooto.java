package net.moyokoo.diooto;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.SketchImageView;

public class DragDiooto {
    Context mContext;

    public DragDiooto(Context context) {
        mContext = context;
        contentViewConfig = new ContentViewConfig();
    }

    private ContentViewConfig contentViewConfig;

    public DragDiooto urls(String imageUrl) {
        this.contentViewConfig.setImageUrls(new String[]{imageUrl});
        return this;
    }

    public DragDiooto fullscreen(boolean isFullScreen) {
        this.contentViewConfig.setFullScreen(isFullScreen);
        return this;
    }

    public DragDiooto urls(String[] imageUrls) {
        this.contentViewConfig.setImageUrls(imageUrls);
        return this;
    }

    public DragDiooto type(int type) {
        this.contentViewConfig.setType(type);
        return this;
    }


    public DragDiooto position(int position) {
        this.contentViewConfig.setPosition(position);
        return this;
    }

    public DragDiooto views(View view) {
        View[] views = new View[1];
        views[0] = view;
        return views(views);
    }

    public DragDiooto views(View[] views) {
        List<ContentViewOriginModel> list = new ArrayList<>();
        for (View imageView : views) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            int location[] = new int[2];
            imageView.getLocationOnScreen(location);
            imageBean.left = location[0];
            imageBean.top = location[1];
            imageBean.width = imageView.getWidth();
            imageBean.height = imageView.getHeight();
            list.add(imageBean);
        }
        contentViewConfig.setContentViewOriginModels(list);
        return this;
    }


    public DragDiooto start() {
        if ((((Activity) (mContext)).getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            contentViewConfig.setFullScreen(true);
        }
        if (!contentViewConfig.isFullScreen()) {
            StatusUtil.with((Activity) mContext).hideStatus(mContext);
        }
        ImageActivity.startImageActivity((Activity) mContext, contentViewConfig);
        return this;
    }

    public DragDiooto loadPhotoBeforeShowBigImage(DragDiooto.OnLoadPhotoBeforeShowBigImage on) {
        onLoadPhotoBeforeShowBigImage = on;
        return this;
    }

    public DragDiooto onVideoLoadEnd(DragDiooto.OnShowToMaxFinish on) {
        onShowToMaxFinish = on;
        return this;
    }

    public DragDiooto onFinish(DragDiooto.OnFinish on) {
        onFinish = on;
        return this;
    }

    public DragDiooto onProvideVideoView(DragDiooto.OnProvideVideoView on) {
        onProvideVideoView = on;
        return this;
    }

    public static OnLoadPhotoBeforeShowBigImage onLoadPhotoBeforeShowBigImage;
    public static OnShowToMaxFinish onShowToMaxFinish;
    public static OnProvideVideoView onProvideVideoView;
    public static OnFinish onFinish;

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

    public void e(String msg) {
        Log.e("1", msg);
    }
}
