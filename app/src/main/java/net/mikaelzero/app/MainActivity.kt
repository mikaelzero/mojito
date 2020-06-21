package net.mikaelzero.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import net.mikaelzero.app.local.LocalImageActivity
import net.mikaelzero.mojito.Mojito

class MainActivity : AppCompatActivity() {
    var texts = listOf(
        "load with glide",
        "load with fresco",
        "load local image with glide",
        "load local image with fresco",
        "load target image by button",
        "Activity Cover",
        "clean cache"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = MainAdapter()
        adapter.setList(texts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { adapter, view, position ->
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
                    Mojito.cleanCache()
                }
            }
        }
    }

}