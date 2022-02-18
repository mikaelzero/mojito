package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import net.mikaelzero.app.databinding.ActivityDisplayBinding
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.ext.mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.NumIndicator
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.InstanceLoader

class PreviewActivity : AppCompatActivity() {
    var context: Context? = null
    private lateinit var binding: ActivityDisplayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getNormalImages())
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            binding.recyclerView.mojito(R.id.srcImageView) {
                urls(SourceUtil.getNormalImages())
                position(position, headerSize = 1, footerSize = 1)
                autoLoadTarget(false)
                setProgressLoader(object : InstanceLoader<IProgress> {
                    override fun providerInstance(): IProgress {
                        return DefaultPercentProgress()
                    }
                })
                errorDrawableResId(1, R.drawable.comment)
                mojitoListener(
                    onClick = { view, x, y, pos ->
                        Toast.makeText(context, "tap click", Toast.LENGTH_SHORT).show()
                    }
                )
                progressLoader {
                    DefaultPercentProgress()
                }
                setIndicator(NumIndicator())
            }
        }
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))

        binding.singleIv.addImg(SourceUtil.getSingleImage())
        binding.longHorIv.addImg(SourceUtil.getLongHorImage())

        binding.singleIv.setOnClickListener {
            Mojito.start(this) {
                urls(SourceUtil.getSingleImage())
                views(binding.singleIv)
            }
        }
        binding.longHorIv.setOnClickListener {
            binding.longHorIv.mojito(SourceUtil.getLongHorImage())
        }

        binding.noViewBtn.setOnClickListener {
            Mojito.start(this) {
                urls(SourceUtil.getSingleImage())
            }
        }
        binding.noViewViewPagerBtn.setOnClickListener {
            Mojito.start(this) {
                urls(SourceUtil.getNormalImages())
                    .setIndicator(NumIndicator())
            }
        }
    }
}