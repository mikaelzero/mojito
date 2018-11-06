
package net.moyokoo.diooto;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class StatusUtil {
    private StatusUtil() {
    }

    public static HostLayout with(Activity activity) {
        return with(activity, false);
    }

    public static HostLayout with(Activity activity, boolean isFullScreen) {
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