package com.creative.pexels.usecase

import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.dispatchers.AppDispatchers
import com.creative.pexels.ui.search.EmptyPlaceHolderUiState
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

    private var isLoading: Boolean = false
    private var currentPage: Int = 1
    private var totalPhotoCount: Int = Int.MAX_VALUE
    private var currentQuery: String = ""
        set(value) {
            if (field != value) {
                field = value
                internalResetState()
            }
        }
    private val mutex: Mutex = Mutex()
    private val mutablePhotoFlow: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    private val mutableEmptyStateFlow: MutableStateFlow<EmptyPlaceHolderUiState> = MutableStateFlow(
        EmptyPlaceHolderUiState(
            text = "Try to search something. The results will be very appealing!",
            lottieResId = com.creative.pexels.R.raw.empty_lottie
        )
    )

    val photoFlow: Flow<List<Photo>>
        get() = mutablePhotoFlow.asStateFlow()
    val emptyStateFlow: Flow<EmptyPlaceHolderUiState>
        get() = mutableEmptyStateFlow.asStateFlow()

    suspend fun loadPhotos(query: String): Result<Int> = withContext(appDispatchers.io) {
        if (isLoading) {
            return@withContext Result.success(0)
        }
        mutex.withLock {
            currentQuery = query
            if (totalPhotoCount <= mutablePhotoFlow.value.size) {
                return@withLock Result.failure(IllegalStateException("No more photos to load"))
            }
            runCatching {
                isLoading = true
                photoDataSource.queryPhoto(currentQuery, currentPage, DEFAULT_PAGE_SIZE)
            }.fold({
                currentPage += 1
                totalPhotoCount = it.totalResults
                mutablePhotoFlow.value += it.photos
                isLoading = false
                Result.success(it.photos.size)
            }, {
                if (mutablePhotoFlow.value.isEmpty()) {
                    mutableEmptyStateFlow.value = EmptyPlaceHolderUiState(
                        text = "Something went wrong while we trying to get the photos. Please try it again : )",
                        lottieResId = com.creative.pexels.R.raw.empty_lottie
                    )
                }
                isLoading = false
                Result.failure(Exception("Something went wrong while we trying to fetch photos.", it))
            })
        }
    }

    suspend fun loadMoreCurrentQuery(): Result<Int> = withContext(appDispatchers.io) {
        if (currentQuery.isEmpty()) {
            internalResetState()
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

    private fun internalResetState() {
        currentPage = 1
        totalPhotoCount = Int.MAX_VALUE
        mutablePhotoFlow.value = emptyList()
        mutableEmptyStateFlow.value = EmptyPlaceHolderUiState(
            text = "Loading your new keyword. The results will be very appealing!",
            lottieResId = com.creative.pexels.R.raw.empty_lottie
        )
    }
}
