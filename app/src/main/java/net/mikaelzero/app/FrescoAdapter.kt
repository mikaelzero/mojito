package net.mikaelzero.app

import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.mikaelzero.mojito.tools.ScreenUtils


/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 8:43 PM
 * @Description:
 */
class FrescoAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_grid_fresco) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val srcImageView = holder.getView<ImageView>(R.id.srcImageView)
        (srcImageView.layoutParams as FrameLayout.LayoutParams).width = ScreenUtils.getScreenWidth(context) / 3
        (srcImageView.layoutParams as FrameLayout.LayoutParams).height = ScreenUtils.getScreenWidth(context) / 3
        val uri = Uri.parse(item)
        srcImageView.setImageURI(uri)
    }
}