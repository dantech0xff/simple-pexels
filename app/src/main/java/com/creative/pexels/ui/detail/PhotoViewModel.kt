package com.creative.pexels.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.pexels.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@HiltViewModel
class PhotoViewModel @Inject constructor() : ViewModel(), IPhotoViewModel {

    private val mutableMessageSharedFlow: MutableSharedFlow<String> = MutableSharedFlow()
    override val messageSharedFlow: SharedFlow<String>
        get() = mutableMessageSharedFlow

    private val mutableIsFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isFavorite: Flow<Boolean>
        get() = mutableIsFavorite

    override fun toggleFavorite(photo: Photo) {
        mutableIsFavorite.value = !mutableIsFavorite.value
    }

    override fun download(photo: Photo) {
        viewModelScope.launch {
            val url = photo.original
            mutableMessageSharedFlow.emit("Feature is under development: Downloading from $url")
        }
    }
}
