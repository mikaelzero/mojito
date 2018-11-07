package net.moyokoo.diooto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.moyokoo.drag.R;

import me.panpf.sketch.SketchImageView;

public class ImageFragment extends Fragment {
    DragDiootoView dragDiootoView;
    ContentViewOriginModel contentViewOriginModel;

    public static ImageFragment newInstance(String url, ContentViewOriginModel contentViewOriginModel) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putParcelable("model", contentViewOriginModel);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        String url = getArguments().getString("url");
        dragDiootoView = view.findViewById(R.id.dragDiootoView);
        SketchImageView photoView = new SketchImageView(getContext());
        photoView.getOptions().setDecodeGifImage(true);
        photoView.setZoomEnabled(true);
        photoView.displayImage(url);
        dragDiootoView.addContentChildView(photoView);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        ((SketchImageView) (dragDiootoView.getContentView())).getZoomer().getBlockDisplayer().setPause(hidden);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contentViewOriginModel = getArguments().getParcelable("model");
        dragDiootoView.setOnShowFinishListener(new DragDiootoView.OnShowFinishListener() {
            @Override
            public void showFinish(DragDiootoView view, boolean showImmediately) {
                ((SketchImageView) dragDiootoView.getContentView()).displayImage("http://bmob-cdn-982.b0.upaiyun.com/2017/02/24/98754a6a401d5c48806b2b3863e32bed.jpg");
            }
        });
        dragDiootoView.putData(contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(), contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight());
        dragDiootoView.show();
    }
}
