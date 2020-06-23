package net.mikaelzero.app.video

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_target.recyclerView
import kotlinx.android.synthetic.main.activity_video.*
import net.mikaelzero.app.GlideAdapter
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.MultiContentLoader
import net.mikaelzero.mojito.loader.glide.GlideImageLoader
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory

class VideoActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_video)
        ImmersionBar.with(this).transparentBar().init()
        Mojito.initialize(
            GlideImageLoader.with(this),
            SketchImageLoadFactory()
        )
        Glide.with(this).load(SourceUtil.getSingleVideoImages()).into(singleVideoIv)
        singleVideoIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleVideoImages(), SourceUtil.getSingleVideoTargetImages())
                .setMultiContentLoader(object : MultiContentLoader {
                    override fun providerLoader(position: Int): ImageViewLoadFactory {
                        return ArtLoadFactory()
                    }

                    override fun providerEnableTargetLoad(position: Int): Boolean {
                        return false
                    }
                })
                .views(singleVideoIv)
                .start()
        }
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = GlideAdapter()
        adapter.setList(SourceUtil.getVideoImages())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.with(context)
                .urls(SourceUtil.getVideoImages(), SourceUtil.getVideoTargetImages())
                .setMultiContentLoader(object : MultiContentLoader {
                    override fun providerLoader(position: Int): ImageViewLoadFactory {
                        return if (position == 1 || position == 2) {
                            ArtLoadFactory()
                        } else {
                            SketchImageLoadFactory()
                        }
                    }

                    override fun providerEnableTargetLoad(position: Int): Boolean {
                        return position == 0 || position == 3
                    }
                })
                .position(position)
                .views(recyclerView, R.id.srcImageView)
                .start()
        }

    }
}