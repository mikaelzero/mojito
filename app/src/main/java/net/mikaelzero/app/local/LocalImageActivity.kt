package net.mikaelzero.app.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_local.*
import net.mikaelzero.app.NumCoverLoader
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.app.TargetImageCover
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.DefaultPercentProgress
import net.mikaelzero.mojito.impl.SimpleMojitoViewCallback
import net.mikaelzero.mojito.interfaces.IProgress
import net.mikaelzero.mojito.loader.ImageCoverLoader
import net.mikaelzero.mojito.loader.InstanceLoader
import java.util.*

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
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        } else {
            loadAdapter()
        }
    }

    private fun loadAdapter() {
        images = SourceUtil.getLatestPhotoPaths(this, 99)
        if (images != null && !images.isNullOrEmpty()) {
            val localAdapter = LocalAdapter()
            localAdapter.setList(images)
            recyclerView.adapter = localAdapter
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            localAdapter.setOnItemClickListener { adapter, view, position ->
                Mojito.with(this)
                    .urls(images)
                    .position(position)
                    .views(recyclerView, R.id.srcImageView)
                    .setCoverLayoutLoader(NumCoverLoader())
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