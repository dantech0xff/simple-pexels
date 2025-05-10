package com.creative.pexels.data.source

import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.model.QueryPageResult

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

interface PhotoDataSource {
    suspend fun queryPhoto(query: String, pageIndex: Int, pageSize: Int): QueryPageResult
}