package com.creative.pexels.data.adapter

import com.creative.pexels.data.model.Photo
import com.creative.pexels.network.response.PexelsPhoto

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

fun PexelsPhoto.toPhoto(): Photo {
    return Photo(
        id = id,
        photographer = photographer,
        photographerUrl = photographerUrl,
        width = width,
        height = height,
        original = src.original,
        thumb = src.medium
    )
}