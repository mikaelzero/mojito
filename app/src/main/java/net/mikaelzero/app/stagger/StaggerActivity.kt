package net.mikaelzero.app.stagger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.app.databinding.ActivityStaggerBinding
import net.mikaelzero.mojito.Mojito

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/22 8:38 PM
 * @Description:
 */
class StaggerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaggerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaggerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        val adapter = StaggerAdapter()
        binding.recyclerView.adapter = adapter
        adapter.setList(SourceUtil.getNormalImages())
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.start(this) {
                urls(SourceUtil.getNormalImages())
                position(position)
                views(binding.recyclerView, R.id.srcImageView)
            }

        }
    }
}