package com.creative.pexels.data.model

data class Photo(
    val id: Long,
    val width: Int = 0,
    val height: Int = 0,
    val photographer: String = "",
    val photographerUrl: String = "",
    val original: String = "",
    val thumb: String = ""
)
