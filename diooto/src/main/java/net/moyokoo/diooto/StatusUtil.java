
package net.moyokoo.diooto;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class StatusUtil {
    private StatusUtil() {
    }

    public static HostLayout with(FragmentActivity activity) {
        return with(activity, false);
    }

    public static HostLayout with(FragmentActivity activity, boolean isFullScreen) {
        Window window = activity.getWindow();
        ViewGroup contentLayout = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        if (contentLayout.getChildCount() > 0) {
            View contentView = contentLayout.getChildAt(0);
            if (contentView instanceof HostLayout) {
                return (HostLayout) contentView;
            }
        }
        return new HostLayout(activity, isFullScreen);
    }
}