package net.mikaelzero.mojito.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/23 10:06 AM
 * @Description:
 */
@Parcelize
data class FragmentConfig(
    var originUrl: String,
    var targetUrl: String? = null,
    var viewParams: ViewParams? = null,
    var position: Int,
    var autoLoadTarget: Boolean = true,
    var showImmediately: Boolean,
    var errorDrawableResId: Int = 0
) : Parcelable