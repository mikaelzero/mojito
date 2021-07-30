package net.mikaelzero.app.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import net.mikaelzero.app.R
import net.mikaelzero.app.SourceUtil
import net.mikaelzero.app.databinding.ActivityLocalBinding
import net.mikaelzero.mojito.Mojito
import net.mikaelzero.mojito.impl.NumIndicator

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/19 5:19 PM
 * @Description:
 */
class LocalImageActivity : AppCompatActivity() {

    private var images: List<String>? = null
    private lateinit var binding: ActivityLocalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            val localAdapter = LocalImageAdapter()
            localAdapter.setList(images)
            binding.recyclerView.adapter = localAdapter
            binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
            localAdapter.setOnItemClickListener { adapter, view, position ->
                Mojito.start(this) {
                    urls(images)
                    position(position)
                    views(binding.recyclerView, R.id.srcImageView)
                    setIndicator(NumIndicator())
                }

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            loadAdapter()
        } else {
            Toast.makeText(this, "请允许获取相册图片文件访问权限", Toast.LENGTH_SHORT).show()
        }
    }


}