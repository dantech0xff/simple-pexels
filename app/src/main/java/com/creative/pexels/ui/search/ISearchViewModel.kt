package com.creative.pexels.ui.search

import com.creative.pexels.R
import com.creative.pexels.data.model.Photo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface ISearchViewModel {
    val messageSharedFlow: SharedFlow<String>
    val uiState: Flow<SearchScreenUiState>
    fun querySearch(query: String): Job
    suspend fun loadMoreCurrentQuery(): Job
    fun setSearchKeyword(keyword: String)
}

data class SearchScreenUiState(
    val searchPhotos: List<Photo>,
    val trendKeywords: List<String>,
    val searchKeyword: String,
    val isLoading: Boolean,
    val errorMessage: String?,
    val emptyPlaceHolderUiState: EmptyPlaceHolderUiState
) {
    companion object {
        fun empty() = SearchScreenUiState(
            searchPhotos = emptyList(),
            trendKeywords = emptyList(),
            searchKeyword = "",
            isLoading = true,
            errorMessage = null,
            emptyPlaceHolderUiState = EmptyPlaceHolderUiState(
                text = "Try to search something. The results will be very appealing!",
                lottieResId = R.raw.empty_lottie
            )
        )
    }
}

data class EmptyPlaceHolderUiState(
    val text: String,
    val lottieResId: Int
)