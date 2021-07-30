package net.mikaelzero.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.imageLoader
import coil.util.CoilUtils
import com.bumptech.glide.Glide
import com.facebook.drawee.backends.pipeline.Fresco
import net.mikaelzero.app.databinding.ActivityMainBinding
import net.mikaelzero.app.local.LocalImageActivity
import net.mikaelzero.app.stagger.StaggerActivity
import net.mikaelzero.app.video.VideoActivity
import net.mikaelzero.coilimageloader.CoilImageLoader
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.loader.fresco.FrescoImageLoader
import net.mikaelzero.mojito.loader.glide.GlideImageLoader
import net.mikaelzero.mojito.view.sketch.SketchImageLoadFactory

class MainActivity : AppCompatActivity() {

    enum class LoaderType {
        Coil, Glide, Fresco
    }

    enum class StartType {
        Normal,
        Local,
        Origin,
        Cover,
        Stagger,
        Scenes,
        Video
    }

    companion object {
        var loaderType: LoaderType = LoaderType.Coil
        fun setImageLoader(applicationContext: Context, type: LoaderType) {
            loaderType = type
            when (loaderType) {
                LoaderType.Coil -> {
                    Mojito.initialize(
                        CoilImageLoader.with(applicationContext),
                        SketchImageLoadFactory()
                    )
                }
                LoaderType.Glide -> {
                    Mojito.initialize(
                        GlideImageLoader.with(applicationContext),
                        SketchImageLoadFactory()
                    )
                }
                LoaderType.Fresco -> {
                    Mojito.initialize(
                        FrescoImageLoader.with(applicationContext),
                        SketchImageLoadFactory()
                    )
                }
            }
        }
    }

    var startType: StartType = StartType.Normal

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setImageLoader(applicationContext, LoaderType.Coil)
        binding.coil.isChecked = true
        binding.loaderRb.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.coil -> {
                    setImageLoader(applicationContext, LoaderType.Coil)
                }
                R.id.glide -> {
                    setImageLoader(applicationContext, LoaderType.Glide)
                }
                R.id.fresco -> {
                    setImageLoader(applicationContext, LoaderType.Fresco)
                }
            }
        }
        binding.normal.isChecked = true
        binding.functionRg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.normal -> {
                    startType = StartType.Normal
                }
                R.id.local -> {
                    startType = StartType.Local
                }
                R.id.origin -> {
                    startType = StartType.Origin
                }
                R.id.cover -> {
                    startType = StartType.Cover
                }
                R.id.stagger -> {
                    startType = StartType.Stagger
                }
                R.id.scenes -> {
                    startType = StartType.Scenes
                }
                R.id.video -> {
                    startType = StartType.Video
                }
            }
        }
        binding.startFb.setOnClickListener {
            when (startType) {
                StartType.Normal -> {
                    startActivity(Intent(this@MainActivity, PreviewActivity::class.java))
                }
                StartType.Local -> {
                    startActivity(Intent(this@MainActivity, LocalImageActivity::class.java))
                }
                StartType.Origin -> {
                    startActivity(Intent(this@MainActivity, TargetActivity::class.java))
                }
                StartType.Cover -> {
                    startActivity(Intent(this@MainActivity, ActivityCoverActivity::class.java))
                }
                StartType.Stagger -> {
                    startActivity(Intent(this@MainActivity, StaggerActivity::class.java))
                }
                StartType.Scenes -> {
                    startActivity(Intent(this@MainActivity, DifferentScenesActivity::class.java))
                }
                StartType.Video -> {
                    startActivity(Intent(this@MainActivity, VideoActivity::class.java))
                }
            }
        }
        binding.cacheBt.setOnClickListener {
            imageLoader.memoryCache.clear()
            CoilUtils.createDefaultCache(this).directory.delete()
            CoilImageLoader.with(applicationContext).cleanCache()
            Glide.get(this).clearMemory()
            Thread { Glide.get(this).clearDiskCache() }.start()
            GlideImageLoader.with(applicationContext).cleanCache()
            val imagePipeline = Fresco.getImagePipeline()
            imagePipeline.clearMemoryCaches()
            imagePipeline.clearDiskCaches()
            imagePipeline.clearCaches()
            FrescoImageLoader.with(applicationContext).cleanCache()
        }
    }

}