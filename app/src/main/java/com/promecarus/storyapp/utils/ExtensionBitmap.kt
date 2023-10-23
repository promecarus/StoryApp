package com.promecarus.storyapp.utils

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270
import androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90
import androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
import androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION
import com.promecarus.storyapp.utils.ImageUtils.rotateImage
import java.io.File

fun Bitmap.getRotatedBitmap(file: File) =
    when (ExifInterface(file).getAttributeInt(TAG_ORIENTATION, ORIENTATION_UNDEFINED)) {
        ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ORIENTATION_NORMAL -> this
        else -> this
    }
