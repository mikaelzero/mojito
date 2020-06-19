package net.mikaelzero.mojito.bean

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/12 7:16 PM
 * @Description:
 */
data class ViewPagerBean(
    val url: String,
    val targetUrl: String? = null,
    val position: Int,
    val showImmediately: Boolean,
    val contentViewOriginModel: ContentViewOriginModel? = null
)