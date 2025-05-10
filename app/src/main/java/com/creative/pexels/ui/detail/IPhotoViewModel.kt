package com.creative.pexels.ui.detail

import com.creative.pexels.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface IPhotoViewModel {
    val messageSharedFlow: SharedFlow<String>
    val isFavorite: Flow<Boolean>
    fun toggleFavorite(photo: Photo)
    fun download(photo: Photo)
}