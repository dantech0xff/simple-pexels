package com.creative.pexels.ui.detail

import com.creative.pexels.data.model.Photo

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface IPhotoViewModel {
    suspend fun findPhotoById(id: Long): Photo?
}