package net.mikaelzero.mojito.impl;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import net.mikaelzero.mojito.interfaces.IProgress;

/**
 * Created by moyokoo.
 * Date:  2018/10/17
 * 进度加载
 */
public class DefaultPercentProgress implements IProgress {
//    private SparseArray<LoadingView> progressBarArray = new SparseArray<>();

    LoadingView loadingView;

    @Override
    public void attach(int position, FrameLayout parent) {
        Context context = parent.getContext();
        int progressSize = LoadingView.dip2Px(context, 50);
        FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(progressSize, progressSize);
        progressLp.gravity = Gravity.CENTER;
         loadingView = new LoadingView(context);
        loadingView.setLayoutParams(progressLp);
        parent.addView(loadingView);
        Log.e("attach", "attach:  position: " + position + "    loadingView:" + loadingView.hashCode());
//        progressBarArray.put(position, loadingView);
    }

    @Override
    public void onStart(int position) {

    }

    @Override
    public void onProgress(int position, int progress) {
//        LoadingView loadingView = progressBarArray.get(position);
        if (loadingView != null) {
            Log.e("attach", "attach onProgress:  position: " + position + "    loadingView:" + loadingView.hashCode()+
                    "     progress:"+progress);
            loadingView.setProgress(progress);
        }
    }

    @Override
    public void onFinish(int position) {
//        LoadingView loadingView = progressBarArray.get(position);

        loadingView.loadCompleted();
    }

    @Override
    public void onFailed(int position) {
//        LoadingView loadingView = progressBarArray.get(position);
        loadingView.loadFaild();
    }

    @Override
    public View getProgressView(int position) {
        return null;
    }
}
