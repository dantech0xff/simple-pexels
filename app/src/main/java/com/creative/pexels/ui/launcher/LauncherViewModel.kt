package com.creative.pexels.ui.launcher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.source.PhotoDataSource
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
class LauncherViewModel @Inject constructor(
    private val dataSource: PhotoDataSource
) : ViewModel(), ILauncherViewModel {

    private val mutableTrendingSearch: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    override val searchPhotos: Flow<List<Photo>>
        get() = dataSource.photoFlow
    override val trendSearch: Flow<List<String>>
        get() = mutableTrendingSearch

    override fun querySearch(query: String) {
        super.querySearch(query)
        viewModelScope.launch {
            dataSource.loadPhotos(query)
        }
    }
}