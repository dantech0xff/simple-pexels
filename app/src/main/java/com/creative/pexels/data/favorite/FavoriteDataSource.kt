package com.creative.pexels.data.favorite

/**
 * Created by dan on 11/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface FavoriteDataSource {
    suspend fun isPhotoFavorite(photoId: Long): Boolean
    suspend fun updatePhotoFavorite(photoId: Long, isFavorite: Boolean): Unit
}