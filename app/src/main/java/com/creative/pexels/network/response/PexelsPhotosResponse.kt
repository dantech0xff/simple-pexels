package com.creative.pexels.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PexelsPhotosResponse(
    @Json(name = "total_results") val totalResults: Int,
    @Json(name = "page") val page: Int,
    @Json(name = "per_page") val perPage: Int,
    @Json(name = "photos") val photos: List<PexelsPhoto>,
    @Json(name = "next_page") val nextPage: String?
)

@JsonClass(generateAdapter = true)
data class PexelsPhoto(
    @Json(name = "id") val id: Int,
    @Json(name = "width") val width: Int,
    @Json(name = "height") val height: Int,
    @Json(name = "url") val url: String,
    @Json(name = "photographer") val photographer: String,
    @Json(name = "photographer_url") val photographerUrl: String,
    @Json(name = "photographer_id") val photographerId: Int,
    @Json(name = "avg_color") val avgColor: String,
    @Json(name = "src") val src: PhotoSrc
)

@JsonClass(generateAdapter = true)
data class PhotoSrc(
    @Json(name = "original") val original: String,
    @Json(name = "large") val large: String,
    @Json(name = "medium") val medium: String,
    @Json(name = "small") val small: String,
    @Json(name = "portrait") val portrait: String,
    @Json(name = "landscape") val landscape: String,
    @Json(name = "tiny") val tiny: String
)