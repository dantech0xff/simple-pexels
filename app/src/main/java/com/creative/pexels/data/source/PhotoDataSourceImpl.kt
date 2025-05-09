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

@Singleton
class PhotoDataSourceImpl @Inject constructor(
    private val pexelsApiService: PexelsApiService,
    private val appDispatchers: AppDispatchers
) : PhotoDataSource {
    companion object {
        private const val DEFAULT_PAGE_SIZE: Int = 20
    }

    private var currentPage: Int = 1
    private var totalPhotoCount: Int = 1

    private val mutex: Mutex = Mutex()
    private val mutablePhotoFlow: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    override val photoFlow: Flow<List<Photo>>
        get() = mutablePhotoFlow.asStateFlow()

    override suspend fun loadPhotos(query: String) = withContext(appDispatchers.io) {
        mutex.withLock {
            if (totalPhotoCount <= mutablePhotoFlow.value.size) {
                return@withLock Result.success(0)
            }
            runCatching {
                pexelsApiService.searchPhotos(query, currentPage, perPage = DEFAULT_PAGE_SIZE).apply {
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
}
