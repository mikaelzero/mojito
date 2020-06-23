package net.mikaelzero.app.stagger

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.mikaelzero.app.R
import net.mikaelzero.mojito.tools.ScreenUtils
import kotlin.random.Random

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class StaggerAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_stagger) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val srcImageView = holder.getView<ImageView>(R.id.srcImageView)
        (srcImageView.layoutParams as LinearLayout.LayoutParams).width = ScreenUtils.getScreenWidth(context) / 3
        (srcImageView.layoutParams as LinearLayout.LayoutParams).height = getRandomIntInRange(900, 200)
        Glide.with(context).load(item).into(srcImageView)
    }

    fun getRandomIntInRange(max: Int, min: Int): Int {
        return Random.nextInt(max - min + min) + min
    }
}