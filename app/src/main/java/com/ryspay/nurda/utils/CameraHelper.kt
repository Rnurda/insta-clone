package com.ryspay.nurda.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity){
    var imageUri: Uri? = null
    val REQUEST_CODE = 1
    private val simpleDateFormat = SimpleDateFormat(
        "yyyyMMdd_HHmmss",
        Locale.US
    )

    fun takeCameraPicture() {
        val intent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(activity.packageManager) != null){
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                activity,
                "com.ryspay.nurda.fileprovider",
                imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = activity.getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }
}