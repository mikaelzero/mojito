package net.mikaelzero.mojito.ext

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.MojitoBuilder

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/7/30 9:45 上午
 * @Description:
 */

fun RecyclerView.mojito(itemId: Int, builder: MojitoBuilder.() -> Unit = {}) {
    Mojito.start(this.context) {
        apply(
            builder.apply {
                views(this@mojito, itemId)
            }
        )
    }
}

fun View.mojito(url: String, builder: MojitoBuilder.() -> Unit = {}) {
    Mojito.start(this.context) {
        apply(
            builder.apply {
                urls(url)
                views(this@mojito)
            }
        )

    }
}