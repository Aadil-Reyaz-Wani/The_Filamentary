package com.kashmir.thefilamentary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {
    private const val MAX_DIMENSION = 1080
    private const val COMPRESSION_QUALITY = 75
    private const val IMAGE_DIRECTORY = "print_images"
    
    /**
     * Compresses and saves an image from a Uri to the app's internal storage
     * @return The path to the saved image
     */
    fun saveCompressedImage(context: Context, imageUri: Uri): String? {
        try {
            // Create directory if it doesn't exist
            val directory = File(context.filesDir, IMAGE_DIRECTORY)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            // Create a unique filename
            val filename = "img_${UUID.randomUUID()}.jpg"
            val outputFile = File(directory, filename)
            
            // Get input stream from Uri
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            
            // Decode image size first to determine scaling
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            
            // Calculate scaling factor
            val scale = calculateScaleFactor(options.outWidth, options.outHeight)
            
            // Decode with scaling
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            
            val newInputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, scaledOptions)
            newInputStream?.close()
            
            // Save compressed bitmap
            FileOutputStream(outputFile).use { out ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }
            
            bitmap?.recycle()
            
            return outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Calculate the scaling factor to resize the image to MAX_DIMENSION
     */
    private fun calculateScaleFactor(width: Int, height: Int): Int {
        var scale = 1
        
        if (width > MAX_DIMENSION || height > MAX_DIMENSION) {
            val widthScale = width / MAX_DIMENSION
            val heightScale = height / MAX_DIMENSION
            
            scale = maxOf(widthScale, heightScale)
        }
        
        return scale
    }
    
    /**
     * Deletes an image file from the given path
     */
    fun deleteImage(imagePath: String): Boolean {
        val file = File(imagePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}