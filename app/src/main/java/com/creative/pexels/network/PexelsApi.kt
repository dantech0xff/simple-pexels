package com.creative.pexels.network

import com.creative.pexels.network.response.PexelsPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by dan on 9/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

private const val SEARCH_PHOTOS = "v1/search"

interface PexelsApi {

    @GET(SEARCH_PHOTOS)
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PexelsPhotosResponse
}