package net.moyokoo.diooto.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.moyokoo.drag.R;

public class HostLayout extends RelativeLayout {


    private Activity mActivity;
    private FrameLayout mContentLayout;
    private int activityStatusBarColor = Color.BLACK;
    private StatusView statusView;


    HostLayout(Activity activity) {
        super(activity);
        this.mActivity = activity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityStatusBarColor = activity.getWindow().getStatusBarColor();
        }
        loadView();
        replaceContentView();

        Utils.invasionStatusBar(mActivity);
        Utils.invasionNavigationBar(mActivity);
        Utils.setStatusBarColor(mActivity, Color.TRANSPARENT);
        Utils.setNavigationBarColor(mActivity, Color.TRANSPARENT);

    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int paddingSize = insets.getSystemWindowInsetBottom();
            mContentLayout.setPaddingRelative(0, 0, 0, paddingSize);
            LayoutParams layoutParams = (LayoutParams) mContentLayout.getLayoutParams();
            layoutParams.bottomMargin = 0;
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, 0));
        } else {
            return insets;
        }
    }

    private void loadView() {
        inflate(mActivity, R.layout.host_layout, this);
        mContentLayout = findViewById(R.id.content);
        statusView = findViewById(R.id.statusView);
    }

    private void replaceContentView() {
        Window window = mActivity.getWindow();
        ViewGroup contentLayout = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        if (contentLayout.getChildCount() > 0) {
            View contentView = contentLayout.getChildAt(0);
            contentLayout.removeView(contentView);
            ViewGroup.LayoutParams contentParams = contentView.getLayoutParams();
            mContentLayout.addView(contentView, contentParams.width, contentParams.height);
        }
        contentLayout.addView(this, -1, -1);
    }


    public HostLayout hideStatus(Context context) {
        statusView.setBackgroundColor(activityStatusBarColor);
        setColor((Activity) context, activityStatusBarColor, 0);
        setNavigationBarColor((Activity) context);
        return this;
    }

    void setNavigationBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    void setColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        }
    }

    private int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}