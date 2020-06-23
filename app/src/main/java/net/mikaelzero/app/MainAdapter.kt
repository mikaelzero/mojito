package net.mikaelzero.app

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/20 4:51 PM
 * @Description:
 */
class MainAdapter : BaseQuickAdapter<MainBean, BaseViewHolder>(R.layout.item_main) {
    override fun convert(holder: BaseViewHolder, item: MainBean) {
        holder.setText(R.id.titleTv, item.title)
        holder.setText(R.id.subTitleTv, item.subTitle)
        Glide.with(context).load(R.drawable.item_bg).into(holder.getView(R.id.backIv))
    }
}