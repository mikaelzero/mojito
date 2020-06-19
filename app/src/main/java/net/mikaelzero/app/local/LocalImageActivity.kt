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
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class LocalImageActivity : AppCompatActivity() {

    private var images: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                101
            )
        } else {
            images = SourceUtil.getLatestPhotoPaths(this, 99)
            if (images != null && !images.isNullOrEmpty()) {
                val localAdapter = LocalAdapter()
                localAdapter.setList(images)
                recyclerView.adapter = localAdapter
                recyclerView.layoutManager = GridLayoutManager(this, 3)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101) {
            images = SourceUtil.getLatestPhotoPaths(this, 99)
            if (images != null && !images.isNullOrEmpty()) {
                val localAdapter = LocalAdapter()
                localAdapter.setList(images)
                recyclerView.adapter = localAdapter
                recyclerView.layoutManager = GridLayoutManager(this, 3)
            }
        } else {
            Toast.makeText(this, "请允许获取相册图片文件访问权限", Toast.LENGTH_SHORT).show()
        }
    }


}