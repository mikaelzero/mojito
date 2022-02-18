package net.mikaelzero.mojito.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActivityConfig(
    var originImageUrls: List<String>? = null,
    var targetImageUrls: List<String>? = null,
    var viewParams: List<ViewParams>? = null,
    var position: Int = 0,
    var headerSize: Int = 0,
    var footerSize: Int = 0,
    var autoLoadTarget: Boolean = true,
    var errorDrawableResIdList: HashMap<Int, Int> = hashMapOf()
) : Parcelable