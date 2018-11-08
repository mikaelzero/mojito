package net.moyokoo.diooto.interfaces;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.widget.FrameLayout;

import net.moyokoo.diooto.tools.LoadingView;

public class DefaultProgress implements IProgress {
    private SparseArray<LoadingView> progressBarArray = new SparseArray<>();

    @Override
    public void attach(int position, FrameLayout parent) {
        Context context = parent.getContext();
        int progressSize = LoadingView.dip2Px(context, 50);
        FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(progressSize, progressSize);
        progressLp.gravity = Gravity.CENTER;
        LoadingView loadingView = new LoadingView(context);
        loadingView.setLayoutParams(progressLp);
        parent.addView(loadingView);
        progressBarArray.put(position, loadingView);
    }

    @Override
    public void onStart(int position) {

    }

    @Override
    public void onProgress(int position, int progress) {
        LoadingView loadingView = progressBarArray.get(position);
        if (loadingView != null) {
            loadingView.setProgress(progress);
        }
    }

    @Override
    public void onFinish(int position) {
        LoadingView loadingView = progressBarArray.get(position);
        loadingView.loadCompleted();
    }

    @Override
    public void onFailed(int position) {
        LoadingView loadingView = progressBarArray.get(position);
        loadingView.loadFaild();
    }
}
