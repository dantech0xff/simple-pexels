package com.creative.pexels.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.pexels.usecase.QueryPhotosByKeywordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val queryPhotoUseCase: QueryPhotosByKeywordUseCase
) : ViewModel(), ISearchViewModel {

    private val mutableTrendingSearch: MutableStateFlow<List<String>> = MutableStateFlow(
        listOf(
            "bitcoin", "flower", "american",
            "espresso", "trump", "to the moon"
        )
    )
    private val mutableSearchKeyword: MutableStateFlow<String> = MutableStateFlow("")
    private val mutableIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val uiState: Flow<SearchScreenUiState>
        get() = combine(
            queryPhotoUseCase.photoFlow,
            mutableTrendingSearch,
            mutableSearchKeyword,
            mutableIsLoading,
            queryPhotoUseCase.emptyStateFlow
        ) { photos, trends, keyword, isLoading, emptyState ->
            SearchScreenUiState(
                searchPhotos = photos,
                trendKeywords = trends,
                searchKeyword = keyword,
                errorMessage = null,
                isLoading = isLoading,
                emptyPlaceHolderUiState = emptyState
            )
        }

    override fun querySearch(query: String) = viewModelScope.launch {
        setSearchKeyword(query)
        mutableIsLoading.value = true
        queryPhotoUseCase.loadPhotos(query)
        mutableIsLoading.value = false
    }

    override suspend fun loadMoreCurrentQuery() = viewModelScope.launch {
        mutableIsLoading.value = true
        queryPhotoUseCase.loadMoreCurrentQuery()
        mutableIsLoading.value = false
    }

    override fun setSearchKeyword(keyword: String) {
        mutableSearchKeyword.value = keyword
    }
}
