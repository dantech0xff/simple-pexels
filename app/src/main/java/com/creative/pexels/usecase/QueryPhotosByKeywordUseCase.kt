package com.creative.pexels.usecase

import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.dispatchers.AppDispatchers
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@ViewModelScoped
class QueryPhotosByKeywordUseCase @Inject constructor(
    private val photoDataSource: PhotoDataSource,
    private val appDispatchers: AppDispatchers
) {
    companion object {
        private const val DEFAULT_PAGE_SIZE: Int = 20
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

    val photoFlow: Flow<List<Photo>>
        get() = mutablePhotoFlow.asStateFlow()

    suspend fun loadPhotos(query: String) = withContext(appDispatchers.io) {
        mutex.withLock {
            currentQuery = query
            if (totalPhotoCount <= mutablePhotoFlow.value.size) {
                return@withLock Result.success(0)
            }
            runCatching {
                photoDataSource.queryPhoto(currentQuery, currentPage, DEFAULT_PAGE_SIZE)
            }.fold({
                currentPage += 1
                totalPhotoCount = it.totalResults
                mutablePhotoFlow.value += it.photos
                Result.success(it.photos.size)
            }, {
                Result.failure(it)
            })
        }
    }

    suspend fun loadMoreCurrentQuery(): Result<Int> = withContext(appDispatchers.io) {
        if (currentQuery.isEmpty()) {
            Result.failure(IllegalStateException("Current query is empty"))
        } else {
            loadPhotos(currentQuery)
        }
    }

    suspend fun clearPhotos() = withContext(appDispatchers.io) {
        mutex.withLock {
            currentQuery = ""
            currentPage = 1
            totalPhotoCount = Int.MAX_VALUE
            mutablePhotoFlow.value = emptyList()
        }
    }
}
