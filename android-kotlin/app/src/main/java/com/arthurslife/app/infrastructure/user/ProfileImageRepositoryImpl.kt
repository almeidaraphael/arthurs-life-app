package com.arthurslife.app.infrastructure.user

import android.graphics.Bitmap
import com.arthurslife.app.domain.user.ProfileImageRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProfileImageRepository using local file storage and base64 encoding.
 *
 * This implementation provides efficient image storage with automatic compression and caching.
 * Images are stored both as files for quick access and as base64 for database persistence.
 */
@Singleton
class ProfileImageRepositoryImpl @Inject constructor(
    private val profileImageDataSource: ProfileImageDataSource,
) : ProfileImageRepository {

    override suspend fun storeImage(userId: String, image: Bitmap): String {
        return profileImageDataSource.storeProfileImage(userId, image)
    }

    override suspend fun getImage(userId: String, base64Data: String?): Bitmap? {
        return profileImageDataSource.getProfileImage(userId, base64Data)
    }

    override suspend fun deleteImage(userId: String) {
        profileImageDataSource.deleteProfileImage(userId)
    }

    override suspend fun getStorageUsage(): Long {
        return profileImageDataSource.getStorageUsage()
    }

    override suspend fun cleanupUnusedImages(activeUserIds: Set<String>) {
        profileImageDataSource.cleanupUnusedImages(activeUserIds)
    }
}
