package com.creative.pexels.ui.launcher

import com.creative.pexels.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface ILauncherViewModel {
    val searchPhotos: Flow<List<Photo>>
    val trendSearch: Flow<List<String>>
    fun querySearch(query: String) = Unit
    suspend fun loadMoreCurrentQuery() = Unit
}