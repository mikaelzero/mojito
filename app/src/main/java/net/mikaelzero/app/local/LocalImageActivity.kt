package net.mikaelzero.app.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_local.*
import net.mikaelzero.app.NumActivityCoverLoader
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.mojito.Mojito

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class LocalImageActivity : AppCompatActivity() {
    companion object {
        var imageLoader: Int = 0
    }

    private var images: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        } else {
            loadAdapter()
        }
    }

    private fun loadAdapter() {
        images = SourceUtil.getLatestPhotoPaths(this, 666)
        if (images != null && !images.isNullOrEmpty()) {
            val localAdapter = if (imageLoader == 0) LocalGlideAdapter() else LocalFrescoAdapter()
            localAdapter.setList(images)
            recyclerView.adapter = localAdapter
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            localAdapter.setOnItemClickListener { adapter, view, position ->
                Mojito.with(this)
                    .urls(images)
                    .position(position)
                    .views(recyclerView, R.id.srcImageView)
                    .setActivityCoverLoader(NumActivityCoverLoader())
                    .start()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101) {
            loadAdapter()
        } else {
            Toast.makeText(this, "请允许获取相册图片文件访问权限", Toast.LENGTH_SHORT).show()
        }
    }


}