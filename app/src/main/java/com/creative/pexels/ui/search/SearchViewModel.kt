package com.creative.pexels.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.pexels.data.model.Photo
import com.creative.pexels.usecase.QueryPhotosByKeywordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        Log.d("SearchViewModel", "init $this $queryPhotoUseCase")
    }

    private val mutableTrendingSearch: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val mutableSearchKeyword: MutableStateFlow<String> = MutableStateFlow("")

    override val searchPhotos: Flow<List<Photo>>
        get() = queryPhotoUseCase.photoFlow
    override val trendSearch: Flow<List<String>>
        get() = mutableTrendingSearch
    override val searchKeyword: Flow<String>
        get() = mutableSearchKeyword

    override fun querySearch(query: String) = viewModelScope.launch {
        queryPhotoUseCase.loadPhotos(query)
    }

    override suspend fun loadMoreCurrentQuery() = viewModelScope.launch {
        queryPhotoUseCase.loadMoreCurrentQuery()
    }

    override fun setSearchKeyword(keyword: String) {
        mutableSearchKeyword.value = keyword
    }
}