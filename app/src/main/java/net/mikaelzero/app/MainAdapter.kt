package net.mikaelzero.app

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/20 4:51 PM
 * @Description:
 */
class MainAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_main) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv, item)
    }
}