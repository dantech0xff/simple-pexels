package com.creative.pexels.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creative.pexels.data.favorite.FavoriteDataSource
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
class PhotoViewModel @Inject constructor(
    private val favoriteDataSource: FavoriteDataSource
) : ViewModel(), IPhotoViewModel {

    private var _photo: Photo? = null
    private val mutableMessageSharedFlow: MutableSharedFlow<String> = MutableSharedFlow()
    override val messageSharedFlow: SharedFlow<String>
        get() = mutableMessageSharedFlow

    private val mutableIsFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isFavorite: Flow<Boolean>
        get() = mutableIsFavorite

    override fun toggleFavorite(photo: Photo) = viewModelScope.launch {
        val newValue = !mutableIsFavorite.value
        runCatching {
            favoriteDataSource.updatePhotoFavorite(photo.id, newValue)
        }.onSuccess {
            val message = if (newValue) "Added to favorites" else "Removed from favorites"
            mutableMessageSharedFlow.emit(message)
            mutableIsFavorite.value = newValue
        }.onFailure {
            mutableMessageSharedFlow.emit("Failed to update favorite status: ${it.message}")
        }
    }

    override fun download(photo: Photo) = viewModelScope.launch {
        viewModelScope.launch {
            val url = photo.original
            mutableMessageSharedFlow.emit("Feature is under development: Downloading from $url")
        }
    }

    fun setPhoto(photo: Photo) {
        _photo = photo
        viewModelScope.launch {
            mutableIsFavorite.value = favoriteDataSource.isPhotoFavorite(photo.id)
        }
    }
}
