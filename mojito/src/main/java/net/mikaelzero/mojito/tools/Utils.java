package net.mikaelzero.mojito.tools;

import android.content.Context;

/**
 * @Author: MikaelZero
 * @CreateDate: 2020/6/13 5:41 PM
 * @Description:
 */
public class Utils {

    public static int dip2px(Context c, float dpValue) {
        final float scale = c.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}