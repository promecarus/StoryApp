package com.promecarus.storyapp.utils

import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory.decodeFile
import com.promecarus.storyapp.utils.ImageUtils.MAXIMAL_SIZE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun File.reduceFileImage(): File {
    val file = this
    val bitmap = decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap.compress(JPEG, compressQuality, FileOutputStream(file))
    return file
}
