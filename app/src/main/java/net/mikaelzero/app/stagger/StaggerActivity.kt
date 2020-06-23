package net.mikaelzero.app.stagger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_stagger.*
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.mojito.Mojito

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/22 8:38 PM
 * @Description:
 */
class StaggerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stagger)
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val adapter = StaggerAdapter()
        recyclerView.adapter = adapter
        adapter.setList(SourceUtil.getNormalImages())
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.with(this)
                .urls(SourceUtil.getNormalImages())
                .position(position)
                .views(recyclerView, R.id.srcImageView)
                .start()
        }
    }
}