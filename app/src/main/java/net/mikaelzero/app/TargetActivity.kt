package net.mikaelzero.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_target.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.DefaultTargetFragmentCover
import net.mikaelzero.mojito.impl.NumIndicator
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.FragmentCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader

class TargetActivity : AppCompatActivity() {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_target)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = ImageAdapter()
        adapter.setList(SourceUtil.getTargetButtonSmall())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.with(context)
                .urls(SourceUtil.getTargetButtonSmall(), SourceUtil.getTargetButtonTarget())
                .position(position)
                .views(recyclerView, R.id.srcImageView)
                .autoLoadTarget(false)
                .setFragmentCoverLoader(object : InstanceLoader<FragmentCoverLoader> {
                    override fun providerInstance(): FragmentCoverLoader {
                        return DefaultTargetFragmentCover()
                    }
                })
                .setProgressLoader(object : InstanceLoader<IProgress> {
                    override fun providerInstance(): IProgress {
                        return DefaultPercentProgress()
                    }
                })
                .setIndicator(NumIndicator())
                .start()
        }

    }
}