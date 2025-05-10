package com.creative.pexels.data.adapter

import com.creative.pexels.network.response.PexelsPhoto
import com.creative.pexels.network.response.PhotoSrc
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

class PhotoAdapterTest {
    @Test
    fun `toPhoto maps PexelsPhoto to Photo correctly`() {
        // Arrange
        val pexelsPhoto = PexelsPhoto(
            id = 123,
            width = 1920,
            height = 1080,
            url = "https://www.pexels.com/photo/123",
            photographer = "John Doe",
            photographerUrl = "https://www.pexels.com/photographer/john-doe",
            photographerId = 456,
            avgColor = "#FFFFFF",
            src = PhotoSrc(
                original = "https://images.pexels.com/photos/123/original.jpg",
                large = "https://images.pexels.com/photos/123/large.jpg",
                medium = "https://images.pexels.com/photos/123/medium.jpg",
                small = "https://images.pexels.com/photos/123/small.jpg",
                portrait = "https://images.pexels.com/photos/123/portrait.jpg",
                landscape = "https://images.pexels.com/photos/123/landscape.jpg",
                tiny = "https://images.pexels.com/photos/123/tiny.jpg"
            )
        )

        // Act
        val photo = pexelsPhoto.toPhoto()

        // Assert
        assertEquals(123, photo.id)
        assertEquals(1920, photo.width)
        assertEquals(1080, photo.height)
        assertEquals("John Doe", photo.photographer)
        assertEquals("https://www.pexels.com/photographer/john-doe", photo.photographerUrl)
        assertEquals("https://images.pexels.com/photos/123/original.jpg", photo.original)
        assertEquals("https://images.pexels.com/photos/123/medium.jpg", photo.thumb)
    }
}