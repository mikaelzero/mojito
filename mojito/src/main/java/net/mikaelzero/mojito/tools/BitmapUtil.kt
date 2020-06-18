package net.mikaelzero.mojito.tools

import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 1:19 PM
 * @Description:
 */
class BitmapUtil {
    companion object {

        private fun getImageOrientation(sourcePath: String): Int {
            return try {
                val exifInfo = ExifInterface(sourcePath)
                exifInfo.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
            } catch (ignored: Exception) {
                ExifInterface.ORIENTATION_UNDEFINED
            }
        }

        fun getAdjustSize(sourcePath: String, option: BitmapFactory.Options): Array<Int> {
            return when (getImageOrientation(sourcePath)) {
                ExifInterface.ORIENTATION_ROTATE_90,
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    //90
                    arrayOf(option.outHeight, option.outWidth)
                }
                ExifInterface.ORIENTATION_ROTATE_270,
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    //-90
                    arrayOf(option.outHeight, option.outWidth)
                }
                else -> {
                    arrayOf(option.outWidth, option.outHeight)
                }
            }
        }
    }
}