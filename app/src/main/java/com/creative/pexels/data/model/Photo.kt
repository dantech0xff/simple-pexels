package com.creative.pexels.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: Long,
    val width: Int = 0,
    val height: Int = 0,
    val photographer: String = "",
    val photographerUrl: String = "",
    val original: String = "",
    val thumb: String = ""
)

data class QueryPageResult(
    val photos: List<Photo>,
    val totalResults: Int = 0,
)
