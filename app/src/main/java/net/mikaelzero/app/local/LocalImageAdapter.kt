package net.mikaelzero.app.local

import android.widget.FrameLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.mikaelzero.app.R
import net.mikaelzero.app.addImgLocal
import net.mikaelzero.mojito.tools.ScreenUtils

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class LocalImageAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_local) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val srcImageView = holder.getView<FrameLayout>(R.id.srcImageView)
        (srcImageView.layoutParams as FrameLayout.LayoutParams).width = ScreenUtils.getScreenWidth(context) / 3
        (srcImageView.layoutParams as FrameLayout.LayoutParams).height = ScreenUtils.getScreenWidth(context) / 3
        srcImageView.addImgLocal(item)
    }
}