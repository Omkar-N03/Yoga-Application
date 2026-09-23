package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object YogaMediaUtils {

    fun launchYouTubeTutorial(context: Context, youtubeUrl: String?, poseName: String) {
        val targetUrl = if (!youtubeUrl.isNullOrBlank()) {
            youtubeUrl
        } else {
            "https://www.youtube.com/results?search_query=Yoga+${Uri.encode(poseName)}+Tutorial"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=Yoga+${Uri.encode(poseName)}")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Could not open YouTube link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createImageFile(context: Context, prefix: String = "YOGA_PROOF"): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: File(context.filesDir, "images").apply { mkdirs() }
        return File.createTempFile("${prefix}_${timeStamp}_", ".jpg", storageDir)
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun getFormattedCurrentDate(): String {
        return SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date())
    }
}
