package com.creative.pexels.data.source

import com.creative.pexels.data.adapter.toPhoto
import com.creative.pexels.dispatchers.AppDispatchersTestImpl
import com.creative.pexels.network.PexelsApiService
import com.creative.pexels.network.response.PexelsPhoto
import com.creative.pexels.network.response.PexelsPhotosResponse
import com.creative.pexels.network.response.PhotoSrc
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDataSourceImplTest {
    private lateinit var photoDataSource: PhotoDataSourceImpl
    private lateinit var mockApiService: PexelsApiService
    private val testDispatchers = AppDispatchersTestImpl()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockApiService = mockk()
        photoDataSource = PhotoDataSourceImpl(mockApiService, testDispatchers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadPhotos should return success result when API call succeeds`() = runTest {
        // Arrange
        val query = "nature"
        val mockPhotos = createMockPexelsPhotos(3)
        val photosResponse = PexelsPhotosResponse(
            page = 1,
            perPage = 20,
            photos = mockPhotos,
            totalResults = 100,
            nextPage = "next-page-url",
        )

        coEvery { mockApiService.searchPhotos(query, 1, 20) } returns photosResponse

        // Act
        val result = photoDataSource.loadPhotos(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull())

        // Check flow contains expected photos
        val photos = photoDataSource.photoFlow.first()
        assertEquals(3, photos.size)
        assertEquals(mockPhotos.map { it.toPhoto() }, photos)
    }

    @Test
    fun `loadPhotos should append new photos to existing ones`() = runTest {
        // Arrange - First call
        val query = "nature"
        val mockPhotos1 = createMockPexelsPhotos(3, startId = 1)
        val photosResponse1 = PexelsPhotosResponse(
            page = 1,
            perPage = 20,
            photos = mockPhotos1,
            totalResults = 100,
            nextPage = "next-page-url",
        )

        coEvery { mockApiService.searchPhotos(query, 1, 20) } returns photosResponse1

        // First load
        photoDataSource.loadPhotos(query)

        // Arrange - Second call
        val mockPhotos2 = createMockPexelsPhotos(2, startId = 4)
        val photosResponse2 = PexelsPhotosResponse(
            page = 2,
            perPage = 20,
            photos = mockPhotos2,
            totalResults = 100,
            nextPage = "next-page-url",
        )

        coEvery { mockApiService.searchPhotos(query, 2, 20) } returns photosResponse2

        // Act - Second load
        val result = photoDataSource.loadPhotos(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull())

        // Check flow contains all photos (first load + second load)
        val photos = photoDataSource.photoFlow.first()
        assertEquals(5, photos.size)
        assertEquals(
            mockPhotos1.map { it.toPhoto() } + mockPhotos2.map { it.toPhoto() },
            photos
        )
    }

    @Test
    fun `loadPhotos should return failure result when API call fails`() = runTest {
        // Arrange
        val query = "nature"
        val exception = IOException("Network error")

        coEvery { mockApiService.searchPhotos(query, 1, 20) } throws exception

        // Act
        val result = photoDataSource.loadPhotos(query)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        // Flow should remain empty
        val photos = photoDataSource.photoFlow.first()
        assertTrue(photos.isEmpty())
    }

    private fun createMockPexelsPhotos(count: Int, startId: Int = 1): List<PexelsPhoto> {
        return List(count) { index ->
            val id = startId + index
            PexelsPhoto(
                id = id,
                width = 1000,
                height = 750,
                url = "https://www.pexels.com/photo/$id",
                photographer = "Photographer $id",
                photographerUrl = "https://www.pexels.com/photographer/$id",
                photographerId = id * 10,
                avgColor = "#FFFFFF",
                src = PhotoSrc(
                    original = "https://images.pexels.com/photos/$id/original.jpg",
                    large = "https://images.pexels.com/photos/$id/large.jpg",
                    medium = "https://images.pexels.com/photos/$id/medium.jpg",
                    small = "https://images.pexels.com/photos/$id/small.jpg",
                    portrait = "https://images.pexels.com/photos/$id/portrait.jpg",
                    landscape = "https://images.pexels.com/photos/$id/landscape.jpg",
                    tiny = "https://images.pexels.com/photos/$id/tiny.jpg"
                )
            )
        }
    }

    @Test
    fun `loadPhotos should return success with zero when all photos are loaded`() = runTest {
        // Arrange
        val query = "nature"
        val mockPhotos = createMockPexelsPhotos(3)
        val photosResponse = PexelsPhotosResponse(
            page = 1,
            perPage = 20,
            photos = mockPhotos,
            totalResults = 3, // Setting total results to exactly the number of photos we return
            nextPage = null, // No next page available
        )

        coEvery { mockApiService.searchPhotos(query, 1, 20) } returns photosResponse

        // First load - should load all available photos
        val result1 = photoDataSource.loadPhotos(query)

        // Verify first load was successful
        assertTrue(result1.isSuccess)
        assertEquals(3, result1.getOrNull())

        // Now try to load more photos
        // Act
        val result2 = photoDataSource.loadPhotos(query)

        // Assert
        assertTrue(result2.isSuccess)
        assertEquals(0, result2.getOrNull()) // Should return 0 new photos

        // Check flow still contains only the original photos
        val photos = photoDataSource.photoFlow.first()
        assertEquals(3, photos.size)
        assertEquals(mockPhotos.map { it.toPhoto() }, photos)

        // Verify that we didn't make a second API call
        coVerify(exactly = 0) { mockApiService.searchPhotos(query, 2, 20) }
    }
}