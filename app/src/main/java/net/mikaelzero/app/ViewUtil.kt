package net.mikaelzero.app

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil.load
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import java.io.File

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/7/27 12:12 下午
 * @Description:
 */
class ViewUtil {
}

fun ViewGroup.addImg(item: String) {
    when (MainActivity.loaderType) {
        MainActivity.LoaderType.Coil -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.load(item)
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Glide -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).load(item).into(img)
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Fresco -> {
            val img = SimpleDraweeView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            val uri = Uri.parse(item)
            img.setImageURI(uri)
            addView(img, -1, -1)
        }
    }
}

fun ViewGroup.addImgLocal(item: String) {
    when (MainActivity.loaderType) {
        MainActivity.LoaderType.Coil -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.load(Uri.fromFile(File(item)))
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Glide -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).load(item).into(img)
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Fresco -> {
            val img = SimpleDraweeView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.setImageURI(Uri.fromFile(File(item)))
            addView(img, -1, -1)
        }
    }
}

fun ViewGroup.addImgLocal(item: Int) {
    when (MainActivity.loaderType) {
        MainActivity.LoaderType.Coil -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.load(item)
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Glide -> {
            val img = ImageView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context).load(item).into(img)
            addView(img, -1, -1)
        }
        MainActivity.LoaderType.Fresco -> {
            val img = SimpleDraweeView(this.context)
            img.scaleType = ImageView.ScaleType.CENTER_CROP
            img.setImageResource(item)
            addView(img, -1, -1)
        }
    }
}