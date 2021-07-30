package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import net.mikaelzero.app.databinding.ActivityTargetBinding
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.DefaultTargetFragmentCover
import net.mikaelzero.mojito.impl.NumIndicator

class TargetActivity : AppCompatActivity() {
    var context: Context? = null
    private lateinit var binding: ActivityTargetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityTargetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getTargetButtonSmall())
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.start(context) {
                urls(SourceUtil.getTargetButtonSmall(), SourceUtil.getTargetButtonTarget())
                position(position)
                views(binding.recyclerView, R.id.srcImageView)
                autoLoadTarget(false)
                fragmentCoverLoader {
                    DefaultTargetFragmentCover()
                }
                progressLoader {
                    DefaultPercentProgress()
                }
                setIndicator(NumIndicator())
            }

        }

    }
}