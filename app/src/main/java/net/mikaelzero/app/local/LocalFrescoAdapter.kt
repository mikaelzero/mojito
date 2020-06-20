package net.mikaelzero.app.local

import android.net.Uri
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.mikaelzero.app.R
import net.mikaelzero.mojito.tools.ScreenUtils
import java.io.File

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class LocalFrescoAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_local_fresco) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val srcImageView = holder.getView<ImageView>(R.id.srcImageView)
        (srcImageView.layoutParams as FrameLayout.LayoutParams).width = ScreenUtils.getScreenWidth(context) / 3
        (srcImageView.layoutParams as FrameLayout.LayoutParams).height = ScreenUtils.getScreenWidth(context) / 3
        val uri = Uri.fromFile(File(item))
        srcImageView.setImageURI(uri)
    }
}