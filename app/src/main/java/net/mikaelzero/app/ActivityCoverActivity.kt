package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import net.mikaelzero.app.databinding.ActivityCoverBinding
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.DefaultTargetFragmentCover

class ActivityCoverActivity : AppCompatActivity() {
    var context: Context? = null
    private lateinit var binding: ActivityCoverBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getTargetButtonSmall())
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.start(this) {
                urls(SourceUtil.getTargetButtonSmall(), SourceUtil.getTargetButtonTarget())
                position(position)
                views(binding.recyclerView, R.id.srcImageView)
                autoLoadTarget(false)
                setActivityCoverLoader(BilibiliActivityCoverLoader())
                fragmentCoverLoader {
                    DefaultTargetFragmentCover()
                }
                progressLoader {
                    DefaultPercentProgress()
                }
            }

        }

    }
}