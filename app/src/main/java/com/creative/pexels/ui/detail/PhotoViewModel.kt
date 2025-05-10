package com.creative.pexels.ui.detail

import androidx.lifecycle.ViewModel
import com.creative.pexels.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@HiltViewModel
class PhotoViewModel @Inject constructor(

) : ViewModel(), IPhotoViewModel {
    override suspend fun findPhotoById(id: Long): Photo? {
        TODO("Not yet implemented")
    }
}