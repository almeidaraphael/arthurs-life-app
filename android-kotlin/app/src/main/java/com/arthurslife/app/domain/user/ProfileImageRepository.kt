package com.arthurslife.app.domain.user

import android.graphics.Bitmap

/**
 * Repository interface for profile image operations.
 *
 * This interface defines the contract for storing, retrieving, and managing user profile images.
 * It abstracts the underlying storage mechanism (file system, cloud storage, etc.).
 */
interface ProfileImageRepository {
    /**
     * Stores a profile image for a user.
     *
     * @param userId User ID
     * @param image Bitmap image to store
     * @return Base64 encoded string of the stored image
     */
    suspend fun storeImage(userId: String, image: Bitmap): String

    /**
     * Retrieves a profile image for a user.
     *
     * @param userId User ID
     * @param base64Data Optional base64 data as fallback
     * @return Bitmap image, or null if not found
     */
    suspend fun getImage(userId: String, base64Data: String? = null): Bitmap?

    /**
     * Deletes a user's profile image.
     *
     * @param userId User ID
     */
    suspend fun deleteImage(userId: String)

    /**
     * Gets the storage usage for profile images.
     *
     * @return Size in bytes
     */
    suspend fun getStorageUsage(): Long

    /**
     * Cleans up unused profile images.
     *
     * @param activeUserIds Set of active user IDs
     */
    suspend fun cleanupUnusedImages(activeUserIds: Set<String>)
}
