package net.moyokoo.diooto.interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import net.moyokoo.diooto.tools.LoadingView;

public class DefaultCircleProgress implements IProgress {
    private SparseArray<ProgressBar> progressBarArray = new SparseArray<>();

    @Override
    public void attach(int position, FrameLayout parent) {
        Context context = parent.getContext();
        int progressSize = LoadingView.dip2Px(context, 50);
        FrameLayout.LayoutParams progressLp = new FrameLayout.LayoutParams(progressSize, progressSize);
        progressLp.gravity = Gravity.CENTER;
        ProgressBar loadingView = new ProgressBar(context);
        loadingView.setLayoutParams(progressLp);
        parent.addView(loadingView);
        progressBarArray.put(position, loadingView);
    }

    @Override
    public void onStart(int position) {

    }

    @Override
    public void onProgress(int position, int progress) {
    }

    @Override
    public void onFinish(int position) {
        ProgressBar loadingView = progressBarArray.get(position);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onFailed(int position) {
        ProgressBar loadingView = progressBarArray.get(position);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public View getProgressView(int position) {
        return progressBarArray.get(position);
    }
}
