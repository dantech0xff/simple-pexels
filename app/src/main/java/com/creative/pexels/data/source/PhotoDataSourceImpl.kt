package com.creative.pexels.data.source

import com.creative.pexels.data.adapter.toPhoto
import com.creative.pexels.data.model.QueryPageResult
import com.creative.pexels.dispatchers.AppDispatchers
import com.creative.pexels.network.PexelsApiService
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
/**
 * Implementation of [PhotoDataSource] that loads photos from the Pexels API.
 *
 * This class is responsible for managing the state of the photo list and loading new photos
 * when requested. It uses a mutex to ensure thread safety when accessing and modifying the photo list.
 * Currently, the class supports pagination by keeping track of the current page and total photo count.
 * If the total photo count is less than or equal to the size of the current photo list,
 * If the query changed, the current page is reset to 1.
 * @param pexelsApiService The Pexels API service to use for loading photos.
 * @param appDispatchers The app dispatchers for coroutines.
 */

@Singleton
class PhotoDataSourceImpl @Inject constructor(
    private val pexelsApiService: PexelsApiService,
    private val appDispatchers: AppDispatchers
) : PhotoDataSource {

    override suspend fun queryPhoto(query: String, pageIndex: Int, pageSize: Int) = withContext(appDispatchers.io) {
        val result = pexelsApiService.searchPhotos(query, pageIndex, perPage = pageSize)
        QueryPageResult(result.photos.map {
            it.toPhoto()
        }, result.totalResults)
    }

    override suspend fun isPhotoFavorite(photoId: Long): Boolean {
        return false
    }

    override suspend fun updatePhotoFavorite(photoId: Long, isFavorite: Boolean) {

    }
}
