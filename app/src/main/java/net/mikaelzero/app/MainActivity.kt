package net.mikaelzero.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_main.*
import net.mikaelzero.app.local.LocalImageActivity
import net.mikaelzero.app.stagger.StaggerActivity
import net.mikaelzero.app.video.VideoActivity
import net.mikaelzero.mojito.Mojito

class MainActivity : AppCompatActivity() {
    var titles = listOf(
        "Glide加载器",
        "Fresco加载器",
        "使用Glide加载本地图片",
        "使用Fresco加载本地图片",
        "查看原图按钮功能",
        "Activity 自定义覆盖层",
        "瀑布流",
        "不同场景下的使用",
        "视频，视频和图片共用",
        "清除缓存"
    )
    var subTitles = listOf(
        "load with glide",
        "load with fresco",
        "load local image with glide",
        "load local image with fresco",
        "load target image by button",
        "Activity Cover",
        "Stagger Layout",
        "Different scenes",
        "load Video",
        "clean cache"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImmersionBar.with(this).transparentBar().init()
        val adapter = MainAdapter()
        val list = mutableListOf<MainBean>()
        titles.forEachIndexed { index, s ->
            list.add(MainBean(titles[index], subTitles[index]))
        }
        adapter.setList(list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { _, view, position ->
            when (position) {
                0 -> {
                    PreviewActivity.imageLoader = 0
                    val intent = Intent(this@MainActivity, PreviewActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    PreviewActivity.imageLoader = 1
                    val intent = Intent(this@MainActivity, PreviewActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    LocalImageActivity.imageLoader = 0
                    startActivity(Intent(this@MainActivity, LocalImageActivity::class.java))
                }
                3 -> {
                    LocalImageActivity.imageLoader = 1
                    startActivity(Intent(this@MainActivity, LocalImageActivity::class.java))
                }
                4 -> {
                    startActivity(Intent(this@MainActivity, TargetActivity::class.java))
                }
                5 -> {
                    startActivity(Intent(this@MainActivity, ActivityCoverActivity::class.java))
                }
                6 -> {
                    startActivity(Intent(this@MainActivity, StaggerActivity::class.java))
                }
                7 -> {
                    val intent = Intent(this@MainActivity, DifferentScenesActivity::class.java)
                    startActivity(intent)
                }
                8 -> {
                    startActivity(Intent(this@MainActivity, VideoActivity::class.java))
                }
                9 -> {
                    Mojito.cleanCache()
                }
            }
        }
    }

}