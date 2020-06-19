package net.mikaelzero.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_display.*
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.SimpleMojitoViewCallback
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.ImageCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader
import org.salient.artplayer.MediaPlayerManager

class DisplayActivity : AppCompatActivity() {
    var context: Context? = null
    var activityPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_display)
        activityPosition = intent.getIntExtra("position", 0)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapter = NormalAdapter()
        adapter.setList(SourceUtil.getNormalImages())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            Mojito.with(context)
                .urls(SourceUtil.getNormalImages())
                .position(position)
                .views(recyclerView, R.id.srcImageView)
                .setImageCoverLoader(object : InstanceLoader<ImageCoverLoader> {
                    override fun providerInstance(): ImageCoverLoader {
                        return TargetImageCover("https://i0.hdslb.com/bfs/archive/cb79d0f3b728d4ee3399e44574c85dcfc5bb4225.jpg@412w_232h_1c_100q.jpg")
                    }
                })
                .setProgressLoader(object : InstanceLoader<IProgress> {
                    override fun providerInstance(): IProgress {
                        return DefaultPercentProgress()
                    }
                })
                .setOnMojitoListener(object : SimpleMojitoViewCallback() {
                    override fun onLongClick(fragmentActivity: FragmentActivity?, view: View, x: Float, y: Float, position: Int) {
                        Toast.makeText(context, "长按长按长按", Toast.LENGTH_SHORT).show()
                    }
                })
                .setCoverLayoutLoader(NumCoverLoader())
                .start()
        }
        adapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        adapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.header_layout, null))
        Glide.with(this).load(SourceUtil.getSingleImage()[0]).into(singleIv)
        singleIv.setOnClickListener {
            Mojito.with(context)
                .urls(SourceUtil.getSingleImage())
//                .views(singleIv)
                .start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        MediaPlayerManager.instance().pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerManager.instance().releasePlayerAndView(this)
    }

    companion object {
        fun newIntent(activity: Activity, bundle: Bundle?) {
            val intent = Intent(activity, DisplayActivity::class.java)
            intent.putExtras(bundle!!)
            activity.startActivity(intent)
        }
    }
}