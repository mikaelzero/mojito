package net.moyokoo.diooto;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.moyokoo.drag.R;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DownloadProgressListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.request.LoadResult;
import me.panpf.sketch.util.SketchUtils;

public class ImageFragment extends Fragment {
    DragDiootoView dragDiootoView;
    ContentViewOriginModel contentViewOriginModel;
    String url;
    SketchImageView sketchImageView;
    int position;
    int type = ContentViewConfig.PHOTO;
    LoadingView loadingView;

    public DragDiootoView getDragDiootoView() {
        return dragDiootoView;
    }

    public static ImageFragment newInstance(String url, int position, int type, ContentViewOriginModel contentViewOriginModel) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putInt("position", position);
        args.putInt("type", type);
        args.putParcelable("model", contentViewOriginModel);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            position = getArguments().getInt("position");
            type = getArguments().getInt("type");
            contentViewOriginModel = getArguments().getParcelable("model");
        }
        dragDiootoView = view.findViewById(R.id.dragDiootoView);
        loadingView = view.findViewById(R.id.loadingView);
        if (type == ContentViewConfig.VIDEO) {
            if (DragDiooto.onProvideVideoView == null) {
                throw new RuntimeException("you should set onProvideVideoView first if you use VIDEO");
            }
            if (dragDiootoView.getContentParentView().getChildCount() <= 0) {
                dragDiootoView.addContentChildView(DragDiooto.onProvideVideoView.provideView());
                SketchImageView photoView = new SketchImageView(getContext());
                dragDiootoView.addContentChildView(photoView);
            }
        } else {
            sketchImageView = new SketchImageView(getContext());
            sketchImageView.getOptions().setDecodeGifImage(true);
            sketchImageView.setZoomEnabled(true);
            sketchImageView.displayImage(url);
            dragDiootoView.addContentChildView(sketchImageView);
            sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser());
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (dragDiootoView.getContentView() instanceof SketchImageView) {
            ((SketchImageView) (dragDiootoView.getContentView())).getZoomer().getBlockDisplayer().setPause(hidden);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (DragDiooto.onLoadPhotoBeforeShowBigImage != null) {
            if (dragDiootoView.getContentView() instanceof SketchImageView) {
                DragDiooto.onLoadPhotoBeforeShowBigImage.loadView((SketchImageView) dragDiootoView.getContentView(), position);
            } else if (dragDiootoView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                DragDiooto.onLoadPhotoBeforeShowBigImage.loadView((SketchImageView) dragDiootoView.getContentParentView().getChildAt(1), 0);
                dragDiootoView.getContentParentView().getChildAt(1).setVisibility(View.VISIBLE);
            }
        }
        dragDiootoView.setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
            @Override
            public void showFinish(DragDiootoView view, boolean showImmediately) {
                if (type == ContentViewConfig.VIDEO) {
                    if (dragDiootoView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                        dragDiootoView.getContentParentView().getChildAt(1).setVisibility(View.GONE);
                    }
                    if (DragDiooto.onShowToMaxFinish != null) {
                        DragDiooto.onShowToMaxFinish.onShowToMax(dragDiootoView);
                    }
                } else if (type == ContentViewConfig.PHOTO && view.getContentView() instanceof SketchImageView) {
                    loadImage(sketchImageView);
                }
            }
        });
        dragDiootoView.putData(contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(), contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight());
        dragDiootoView.show();
        dragDiootoView.setOnFinishListener(new DragDiootoView.onFinishListener() {
            @Override
            public void callFinish() {
                if (getContext() instanceof ImageActivity) {
                    ((ImageActivity) getContext()).finishView();
                }
                if (DragDiooto.onFinish != null) {
                    DragDiooto.onFinish.finish(dragDiootoView);
                }
            }
        });
        if (type == ContentViewConfig.PHOTO) {
            dragDiootoView.setOnClickListener(new DragDiootoView.onClickListener() {
                @Override
                public void onClick(DragDiootoView dragDiootoView) {
                    dragDiootoView.backToMin();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dragDiootoView.notifySizeConfig();
    }

    private void loadImage(final SketchImageView sketchImageView) {
        if (getContext() == null || sketchImageView == null) {
            return;
        }
        Sketch.with(getContext()).load(url, new LoadListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onCompleted(@NonNull LoadResult result) {
                loadingView.loadCompleted();
                if (result.getGifDrawable() != null) {
                    result.getGifDrawable().followPageVisible(true, true);
                }
                int w = result.getBitmap().getWidth();
                int h = result.getBitmap().getHeight();
                dragDiootoView.notifySize(w, h);
                sketchImageView.displayImage(url);
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                loadingView.loadFaild();
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
            }
        }).downloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                loadingView.setVisibility(View.VISIBLE);
                int ratio = (int) (completedLength / (float) totalLength * 100);
                loadingView.setProgress(ratio);
            }
        }).commit();
    }

    public void backToMin() {
        dragDiootoView.backToMin();
    }

    /**
     * SketchImageView 生命周期处理
     */

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        // 不可见的时候暂停分块显示器，节省内存，可见的时候恢复
        if (sketchImageView != null && sketchImageView.isZoomEnabled()) {
            sketchImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser);
            Drawable lastDrawable = SketchUtils.getLastDrawable(sketchImageView.getDrawable());
            if (lastDrawable != null && (lastDrawable instanceof SketchGifDrawable)) {
                ((SketchGifDrawable) lastDrawable).followPageVisible(isVisibleToUser, false);
            }
        }
    }
}
