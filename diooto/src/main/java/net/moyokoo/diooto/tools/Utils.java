package net.moyokoo.diooto.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utils {

    /**
     * Set the status bar color.
     */
    public static void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }
    }

    /**
     * Set the navigation bar color.
     */
    public static void setNavigationBarColor(Activity activity, int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(navigationBarColor);
        }
    }

    /**
     * Set the content layout full the StatusBar, but do not hide StatusBar.
     */
    public static void invasionStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Set the content layout full the NavigationBar, but do not hide NavigationBar.
     */
    public static void invasionNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Set the status bar to dark.
     */
    public static boolean setStatusBarDarkFont(Activity activity, boolean darkFont) {
        if (setMIUIStatusBarFont(activity, darkFont)) {
            setDefaultStatusBarFont(activity, darkFont);
            return true;
        } else if (setMeizuStatusBarFont(activity, darkFont)) {
            setDefaultStatusBarFont(activity, darkFont);
            return true;
        } else {
            return setDefaultStatusBarFont(activity, darkFont);
        }
    }

    private static boolean setMeizuStatusBarFont(Activity activity, boolean darkFont) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkFont) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean setMIUIStatusBarFont(Activity activity, boolean dark) {
        Window window = activity.getWindow();
        Class<?> clazz = window.getClass();
        try {
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    private static boolean setDefaultStatusBarFont(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            if (dark) {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            return true;
        }
        return false;
    }
    public static int dip2px(Context c, float dpValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}