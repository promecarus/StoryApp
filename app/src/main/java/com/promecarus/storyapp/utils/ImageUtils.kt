package com.promecarus.storyapp.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns.RELATIVE_PATH
import androidx.core.content.FileProvider.getUriForFile
import com.promecarus.storyapp.BuildConfig.APPLICATION_ID
import com.promecarus.storyapp.R.string.app_name
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ImageUtils {
    const val MAXIMAL_SIZE = 1_000_000
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private var timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }.format(Date())

    fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (SDK_INT >= Q) {
            uri = context.contentResolver.insert(EXTERNAL_CONTENT_URI, ContentValues().apply {
                put(DISPLAY_NAME, "$timeStamp.jpg")
                put(MIME_TYPE, "image/jpeg")
                put(RELATIVE_PATH, "Pictures/${context.getString(app_name)}/")
            })
        }
        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val imageFile = File(
            context.getExternalFilesDir(DIRECTORY_PICTURES),
            "/${context.getString(app_name)}/$timeStamp.jpg"
        )
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return getUriForFile(context, "$APPLICATION_ID.fileprovider", imageFile)
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun uriToFile(context: Context, imageUri: Uri): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(
            buffer, 0, length
        )
        outputStream.close()
        inputStream.close()
        return myFile
    }

    fun rotateImage(source: Bitmap, angle: Float) =
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, Matrix().apply {
            postRotate(angle)
        }, true)
}
