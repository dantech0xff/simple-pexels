package com.creative.pexels.data.model

data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    val photographerUrl: String,
    val original: String,
    val thumb: String
)
