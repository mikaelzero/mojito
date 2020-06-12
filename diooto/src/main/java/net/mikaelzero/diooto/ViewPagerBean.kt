package net.mikaelzero.diooto

import net.mikaelzero.diooto.config.ContentViewOriginModel

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/12 7:16 PM
 * @Description:
 */
data class ViewPagerBean(
    val url: String,
    val position: Int,
    val showImmediately: Boolean,
    val contentViewOriginModel: ContentViewOriginModel
)