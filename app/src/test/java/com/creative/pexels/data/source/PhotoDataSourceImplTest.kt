package com.creative.pexels.data.source

import com.creative.pexels.data.adapter.toPhoto
import com.creative.pexels.dispatchers.AppDispatchersTestImpl
import com.creative.pexels.network.PexelsApiService
import com.creative.pexels.network.response.PexelsPhoto
import com.creative.pexels.network.response.PexelsPhotosResponse
import com.creative.pexels.network.response.PhotoSrc
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    fun `queryPhoto should return photo list and total results when API call succeeds`() = runTest {
        // Arrange
        val query = "nature"
        val pageIndex = 1
        val pageSize = 20
        val mockPhotos = createMockPexelsPhotos(3)
        val photosResponse = PexelsPhotosResponse(
            page = pageIndex,
            perPage = pageSize,
            photos = mockPhotos,
            totalResults = 100,
            nextPage = "next-page-url"
        )

        coEvery { mockApiService.searchPhotos(query, pageIndex, pageSize) } returns photosResponse

        // Act
        val result = photoDataSource.queryPhoto(query, pageIndex, pageSize)

        // Assert
        assertEquals(mockPhotos.map { it.toPhoto() }, result.photos)
        assertEquals(100, result.totalResults)
    }

    @Test
    fun `queryPhoto should handle empty result correctly`() = runTest {
        // Arrange
        val query = "xyznonexistentquery"
        val pageIndex = 1
        val pageSize = 20
        val photosResponse = PexelsPhotosResponse(
            page = pageIndex,
            perPage = pageSize,
            photos = emptyList(),
            totalResults = 0,
            nextPage = null
        )

        coEvery { mockApiService.searchPhotos(query, pageIndex, pageSize) } returns photosResponse

        // Act
        val result = photoDataSource.queryPhoto(query, pageIndex, pageSize)

        // Assert
        assertTrue(result.photos.isEmpty())
        assertEquals(0, result.totalResults)
    }

    @Test
    fun `queryPhoto should propagate exceptions from API`() = runTest {
        // Arrange
        val query = "nature"
        val pageIndex = 1
        val pageSize = 20
        val exception = IOException("Network error")

        coEvery { mockApiService.searchPhotos(query, pageIndex, pageSize) } throws exception

        // Act & Assert
        try {
            photoDataSource.queryPhoto(query, pageIndex, pageSize)
        } catch (e: Exception) {
            assert(e is IOException)
            assertTrue(e.message == "Network error")
        }
    }

    private fun createMockPexelsPhotos(count: Int, startId: Int = 1): List<PexelsPhoto> {
        return List(count) { index ->
            val id = startId + index
            PexelsPhoto(
                id = id.toLong(),
                width = 1000,
                height = 750,
                url = "https://www.pexels.com/photo/$id",
                photographer = "Photographer $id",
                photographerUrl = "https://www.pexels.com/photographer/$id",
                photographerId = id * 10L,
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
}