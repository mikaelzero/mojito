package net.mikaelzero.app.stagger

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.mikaelzero.app.R
import net.mikaelzero.app.addImg
import net.mikaelzero.mojito.tools.ScreenUtils
import kotlin.random.Random

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class StaggerAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_stagger) {
    override fun convert(holder: BaseViewHolder, item: String) {
        val srcImageView = holder.getView<FrameLayout>(R.id.srcImageView)
        (srcImageView.layoutParams as LinearLayout.LayoutParams).width = ScreenUtils.getScreenWidth(context) / 3
        (srcImageView.layoutParams as LinearLayout.LayoutParams).height = getRandomIntInRange(900, 200)
        srcImageView.addImg(item)
    }

    fun getRandomIntInRange(max: Int, min: Int): Int {
        return Random.nextInt(max - min + min) + min
    }
}