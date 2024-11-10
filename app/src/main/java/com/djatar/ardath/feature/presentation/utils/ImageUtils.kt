package com.djatar.ardath.feature.presentation.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ImageUtils {
    private val client = OkHttpClient()

    fun getBitmapFromUrl(url: String): Bitmap? {
        val request = Request.Builder()
            .url(url)
            .build()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                BitmapFactory.decodeStream(inputStream)
            } else null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}