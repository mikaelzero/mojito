package net.moyokoo.diooto.interfaces;

import android.view.View;
import android.widget.FrameLayout;

public interface IProgress {

    void attach(int position, FrameLayout parent);

    void onStart(int position);

    void onProgress(int position, int progress);

    void onFinish(int position);

    void onFailed(int position);

    View getProgressView(int position);

}
