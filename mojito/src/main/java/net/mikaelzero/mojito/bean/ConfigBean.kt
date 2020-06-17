package net.mikaelzero.mojito.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfigBean(
    var originImageUrls: List<String>? = null,
    var targetImageUrls: List<String>? = null,
    var contentViewOriginModels: List<ContentViewOriginModel>? = null,
    var position: Int? = null,
    var headerSize: Int? = null
) : Parcelable