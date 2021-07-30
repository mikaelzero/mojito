package net.mikaelzero.app.video

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.gyf.immersionbar.ImmersionBar
import net.mikaelzero.app.ImageAdapter
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.app.addImg
import net.mikaelzero.app.databinding.ActivityVideoBinding
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.interfaces.ImageViewLoadFactory
import net.mikaelzero.mojito.loader.MultiContentLoader
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory

class VideoActivity : AppCompatActivity() {
    var context: Context? = null
    private lateinit var binding: ActivityVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ImmersionBar.with(this).transparentBar().init()


        binding.singleVideoIv.addImg(SourceUtil.getSingleVideoImages())

        binding.singleVideoIv.setOnClickListener {
            Mojito.start(context) {
                urls(SourceUtil.getSingleVideoImages(), SourceUtil.getSingleVideoTargetImages())
                setMultiContentLoader(object : MultiContentLoader {
                    override fun providerLoader(position: Int): ImageViewLoadFactory {
                        return ArtLoadFactory()
                    }

                    override fun providerEnableTargetLoad(position: Int): Boolean {
                        return false
                    }
                })
                views(binding.singleVideoIv)
            }

        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getVideoImages())
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            Mojito.start(context) {
                urls(SourceUtil.getVideoImages(), SourceUtil.getVideoTargetImages())
                setMultiContentLoader(object : MultiContentLoader {
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
                position(position)
                views(binding.recyclerView, R.id.srcImageView)
            }

        }

    }
}