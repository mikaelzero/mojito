package net.moyokoo.diooto.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.List;

public class Fucking {
    public static int getFuckHeight(Window window) {

        Context context = window.getContext();
        if (hasNotchAtHuawei(context) || hasNotchAtVoio(window.getContext()) || hasNotchInScreenAtOPPO(context)) {
            return getStatusBarHeight(window.getContext());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            final View decorView = window.getDecorView();
            DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                return displayCutout.getSafeInsetTop();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }


    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海

    //vivo不提供接口获取刘海尺寸，目前vivo的刘海宽为100dp,高为27dp
    public static boolean hasNotchAtVoio(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e("Notch", "hasNotchAtVoio ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("Notch", "hasNotchAtVoio NoSuchMethodException");
        } catch (Exception e) {
            Log.e("Notch", "hasNotchAtVoio Exception");
        } finally {
            return ret;
        }
    }

    //OPPO不提供接口获取刘海尺寸，目前其有刘海屏的机型尺寸规格都是统一的。
    // 不排除以后机型会有变化。 其显示屏宽度为1080px，高度为2280px。刘海区域则都是宽度为324px, 高度为80px
    public static boolean hasNotchInScreenAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
}
