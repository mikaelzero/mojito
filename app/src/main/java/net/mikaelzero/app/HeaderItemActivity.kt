package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_display.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.NumIndicator
import net.mikaelzero.mojito.impl.SimpleMojitoViewCallback
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.InstanceLoader
import net.mikaelzero.mojito.loader.fresco.FrescoImageLoader
import net.mikaelzero.mojito.loader.glide.GlideImageLoader
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory

class HeaderItemActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_display)
        Mojito.initialize(
            GlideImageLoader.with(this),
            SketchImageLoadFactory()
        )
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = GlideAdapter2()
        val list = mutableListOf<UrlBean>()
        list.add(UrlBean("", 0))
        list.add(UrlBean("", 0))
        SourceUtil.getNormalImages().forEachIndexed { index, s ->
            list.add(UrlBean(s, 1))
        }
        adapter.setList(list)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            if (position == 0) {
                return@setOnItemClickListener
            }
            Mojito.with(context)
                .urls(SourceUtil.getNormalImages())
                .position(position,2)
                .views(recyclerView, R.id.srcImageView)
                .autoLoadTarget(false)
                .setProgressLoader(object : InstanceLoader<IProgress> {
                    override fun providerInstance(): IProgress {
                        return DefaultPercentProgress()
                    }
                })
                .setOnMojitoListener(object : SimpleMojitoViewCallback() {
                    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
                        Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show()
                    }

                    override fun onClick(view: View, x: Float, y: Float, position: Int) {
                        Toast.makeText(context, "tap click", Toast.LENGTH_SHORT).show()
                    }
                })
                .setIndicator(NumIndicator())
                .start()
        }
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))

        Glide.with(this).load(SourceUtil.getSingleImage()).into(singleIv)
        Glide.with(this).load(SourceUtil.getLongHorImage()).into(longHorIv)

        singleIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleImage())
                .views(singleIv)
                .setOnMojitoListener(object : SimpleMojitoViewCallback() {
                    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
                        Toast.makeText(fragmentActivity, "long click", Toast.LENGTH_SHORT).show()
                    }
                })
                .start()
        }
        longHorIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getLongHorImage())
                .views(longHorIv)
                .start()
        }

        noViewBtn.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleImage())
                .start()
        }
        noViewViewPagerBtn.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getNormalImages())
                .setIndicator(NumIndicator())
                .start()
        }
    }


    companion object {
        var imageLoader: Int = 0
    }
}