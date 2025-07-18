package com.lemonqwest.app.infrastructure.user

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.scale
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for handling profile image operations including storage, compression, and encoding.
 *
 * This class manages both file-based storage for performance and base64 encoding for database storage.
 * Images are automatically compressed and resized to optimize storage space and performance.
 */
@Singleton
class ProfileImageDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val profileImagesDir = File(context.filesDir, "profile_images")

    init {
        if (!profileImagesDir.exists()) {
            profileImagesDir.mkdirs()
        }
    }

    /**
     * Processes and stores a profile image from a bitmap.
     *
     * @param userId User ID to associate with the image
     * @param bitmap Original bitmap image
     * @return Base64 encoded string of the processed image
     */
    suspend fun storeProfileImage(
        userId: String,
        bitmap: Bitmap,
    ): String = withContext(Dispatchers.IO) {
        // Resize and compress the bitmap
        val processedBitmap = resizeAndCompressImage(bitmap)

        // Convert to base64 for database storage
        val base64String = bitmapToBase64(processedBitmap)

        // Also save to file for quick access
        saveImageToFile(userId, processedBitmap)

        base64String
    }

    /**
     * Retrieves a profile image as a bitmap from storage.
     *
     * @param userId User ID
     * @param base64Data Base64 encoded image data (fallback if file doesn't exist)
     * @return Bitmap of the profile image, or null if not found
     */
    suspend fun getProfileImage(
        userId: String,
        base64Data: String? = null,
    ): Bitmap? = withContext(Dispatchers.IO) {
        // Try to load from file first (faster)
        val imageFile = File(profileImagesDir, "$userId.jpg")
        if (imageFile.exists()) {
            return@withContext BitmapFactory.decodeFile(imageFile.absolutePath)
        }

        // Fallback to base64 data
        base64Data?.let { data ->
            return@withContext base64ToBitmap(data)
        }

        null
    }

    /**
     * Deletes a user's profile image from both file and memory.
     *
     * @param userId User ID
     */
    suspend fun deleteProfileImage(userId: String) = withContext(Dispatchers.IO) {
        val imageFile = File(profileImagesDir, "$userId.jpg")
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    /**
     * Converts a bitmap to base64 encoded data URL.
     *
     * @param bitmap Bitmap to convert
     * @return Base64 encoded data URL string
     */
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return "data:image/jpeg;base64,$base64String"
    }

    /**
     * Converts base64 encoded data URL to bitmap.
     *
     * @param base64String Base64 encoded data URL
     * @return Bitmap, or null if conversion fails
     */
    private fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val base64Data = base64String.substringAfter("base64,")
            val byteArray = Base64.decode(base64Data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid Base64 string for bitmap conversion")
            null
        } catch (e: OutOfMemoryError) {
            Timber.e(e, "Out of memory when converting Base64 to bitmap")
            null
        }
    }

    /**
     * Resizes and compresses a bitmap to appropriate dimensions and quality.
     *
     * @param originalBitmap Original bitmap
     * @return Processed bitmap
     */
    private fun resizeAndCompressImage(originalBitmap: Bitmap): Bitmap {
        val originalWidth = originalBitmap.width
        val originalHeight = originalBitmap.height

        // Calculate new dimensions while maintaining aspect ratio
        val ratio = minOf(
            MAX_IMAGE_WIDTH.toFloat() / originalWidth,
            MAX_IMAGE_HEIGHT.toFloat() / originalHeight,
        )

        val newWidth = (originalWidth * ratio).toInt()
        val newHeight = (originalHeight * ratio).toInt()

        // Create scaled bitmap
        return originalBitmap.scale(newWidth, newHeight)
    }

    /**
     * Saves an image to file for quick access.
     *
     * @param userId User ID
     * @param bitmap Bitmap to save
     */
    private fun saveImageToFile(userId: String, bitmap: Bitmap) {
        try {
            val imageFile = File(profileImagesDir, "$userId.jpg")
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            outputStream.close()
        } catch (e: SecurityException) {
            Timber.w(e, "Security exception when saving image to file for user $userId")
            // Log error but don't fail the operation
            // The base64 storage will still work
        } catch (e: java.io.IOException) {
            Timber.e(e, "IO exception when saving image to file for user $userId")
            // Log error but don't fail the operation
            // The base64 storage will still work
        }
    }

    /**
     * Gets the size of stored profile images directory.
     *
     * @return Size in bytes
     */
    suspend fun getStorageUsage(): Long = withContext(Dispatchers.IO) {
        profileImagesDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    /**
     * Cleans up old or unused profile images.
     *
     * @param activeUserIds Set of currently active user IDs
     */
    suspend fun cleanupUnusedImages(activeUserIds: Set<String>) = withContext(Dispatchers.IO) {
        profileImagesDir.listFiles()?.forEach { file ->
            if (file.isFile && file.extension == "jpg") {
                val userId = file.nameWithoutExtension
                if (userId !in activeUserIds) {
                    file.delete()
                }
            }
        }
    }

    companion object {
        private const val MAX_IMAGE_WIDTH = 512
        private const val MAX_IMAGE_HEIGHT = 512
        private const val JPEG_QUALITY = 85
    }
}

/**
 * Exception thrown when image processing operations fail.
 */
class ImageProcessingException(message: String) : Exception(message)
