package net.mikaelzero.mojito.tools

import android.content.Context
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

