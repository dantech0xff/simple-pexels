package com.creative.pexels.data.source

import com.creative.pexels.data.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface PhotoDataSource {

    val photoFlow: Flow<List<Photo>>

    suspend fun loadPhotos(
        query: String
    ): Result<Int>

    suspend fun clearPhotos()
}