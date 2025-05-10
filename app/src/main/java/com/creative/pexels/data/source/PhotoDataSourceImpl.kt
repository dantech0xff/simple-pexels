package com.creative.pexels.data.source

import com.creative.pexels.data.adapter.toPhoto
import com.creative.pexels.data.model.Photo
import com.creative.pexels.dispatchers.AppDispatchers
import com.creative.pexels.network.PexelsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    companion object {
        private const val DEFAULT_PAGE_SIZE: Int = 50
    }

    private var currentPage: Int = 1
    private var totalPhotoCount: Int = Int.MAX_VALUE
    private var currentQuery: String = ""
        set(value) {
            if (field != value) {
                field = value
                currentPage = 1
                totalPhotoCount = Int.MAX_VALUE
                mutablePhotoFlow.value = emptyList()
            }
        }
    private val mutex: Mutex = Mutex()
    private val mutablePhotoFlow: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    override val photoFlow: Flow<List<Photo>>
        get() = mutablePhotoFlow.asStateFlow()

    override suspend fun loadPhotos(query: String) = withContext(appDispatchers.io) {
        mutex.withLock {
            currentQuery = query
            if (totalPhotoCount <= mutablePhotoFlow.value.size) {
                return@withLock Result.success(0)
            }
            runCatching {
                pexelsApiService.searchPhotos(currentQuery, currentPage, perPage = DEFAULT_PAGE_SIZE).apply {
                    totalPhotoCount = totalResults
                }.photos.map {
                    it.toPhoto()
                }
            }.fold({
                currentPage += 1
                mutablePhotoFlow.value += it
                Result.success(it.size)
            }, {
                Result.failure(it)
            })
        }
    }

    override suspend fun clearPhotos() = withContext(appDispatchers.io) {
        mutex.withLock {
            currentQuery = ""
            currentPage = 1
            totalPhotoCount = Int.MAX_VALUE
            mutablePhotoFlow.value = emptyList()
        }
    }
}
