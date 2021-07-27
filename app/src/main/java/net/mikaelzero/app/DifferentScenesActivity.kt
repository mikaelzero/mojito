package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_different_scenes.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.NumIndicator

class DifferentScenesActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_different_scenes)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = DifferentScenesAdapter()
        val list = mutableListOf<UrlBean>()
        list.add(UrlBean("", 0))
        list.add(UrlBean("", 0))
        SourceUtil.getNormalImages().forEachIndexed { index, s ->
            list.add(UrlBean(s, 1))
        }
        adapter.setList(list)
        recyclerView.adapter = adapter
        val newUrls = mutableListOf<String>()
        newUrls.add("")
        newUrls.add("")
        newUrls.addAll(SourceUtil.getNormalImages())
        adapter.setOnItemClickListener { _, view, position ->
            if (position == 0) {
                return@setOnItemClickListener
            }
            Mojito.with(context)
                .urls(newUrls)
                .position(position, headerSize = adapter.headerLayoutCount, footerSize = adapter.footerLayoutCount)
                .views(recyclerView, R.id.srcImageView)
                .setIndicator(NumIndicator())
                .start()
        }
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
    }
}