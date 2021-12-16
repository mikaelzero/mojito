@file:JvmName("CommonExt")

package net.mikaelzero.mojito.tools

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

fun Context?.dp2px(dp: Int): Int {
    if (this == null) {
        return 0
    }
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Context?.dp2px(dp: Float): Int {
    if (this == null) {
        return 0
    }
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Context?.px2dp(px: Int): Int {
    if (this == null) {
        return 0
    }
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

fun View?.dp2px(dp: Int): Int {
    if (this == null) {
        return 0
    }
    if (dp == 0) {
        return 0
    }
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun View?.px2dp(px: Int): Int {
    if (this == null) {
        return 0
    }
    val scale = resources.displayMetrics.density
    return (px / scale + 0.5f).toInt()
}

fun scanForActivity(context: Context?): Activity? {
    if (context == null) return null
    if (context is Activity) {
        return context
    } else if (context is ContextWrapper) {
        return scanForActivity(context.baseContext)
    }
    return null
}