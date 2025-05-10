package com.creative.pexels.usecase

import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.model.QueryPageResult
import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.dispatchers.AppDispatchersTestImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class QueryPhotosByKeywordUseCaseTest {
    private lateinit var useCase: QueryPhotosByKeywordUseCase
    private lateinit var mockPhotoDataSource: PhotoDataSource
    private val testDispatchers = AppDispatchersTestImpl()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockPhotoDataSource = mockk()
        useCase = QueryPhotosByKeywordUseCase(mockPhotoDataSource, testDispatchers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadPhotos should return success result with photo count when API call succeeds`() = runTest {
        // Arrange
        val query = "nature"
        val mockPhotos = createMockPhotos(3)
        val pageResult = QueryPageResult(mockPhotos, 100)

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } returns pageResult

        // Act
        val result = useCase.loadPhotos(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow())
        assertEquals(mockPhotos, useCase.photoFlow.first())
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query, 1, 20) }
    }

    @Test
    fun `loadPhotos should return failure result when API call fails`() = runTest {
        // Arrange
        val query = "nature"
        val exception = IOException("Network error")

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } throws exception

        // Act
        val result = useCase.loadPhotos(query)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        assertEquals(emptyList<Photo>(), useCase.photoFlow.first())
    }

    @Test
    fun `loadPhotos should increment page counter on successful calls`() = runTest {
        // Arrange
        val query = "nature"
        val firstPagePhotos = createMockPhotos(3, 1)
        val secondPagePhotos = createMockPhotos(3, 4)

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } returns QueryPageResult(firstPagePhotos, 100)
        coEvery { mockPhotoDataSource.queryPhoto(query, 2, 20) } returns QueryPageResult(secondPagePhotos, 100)

        // Act
        useCase.loadPhotos(query) // First call
        val secondResult = useCase.loadMoreCurrentQuery() // Second call

        // Assert
        assertTrue(secondResult.isSuccess)
        assertEquals(3, secondResult.getOrThrow())
        assertEquals(firstPagePhotos + secondPagePhotos, useCase.photoFlow.first())
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query, 1, 20) }
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query, 2, 20) }
    }

    @Test
    fun `loadPhotos should reset state when query changes`() = runTest {
        // Arrange
        val query1 = "nature"
        val query2 = "city"
        val firstQueryPhotos = createMockPhotos(3, 1)
        val secondQueryPhotos = createMockPhotos(2, 10)

        coEvery { mockPhotoDataSource.queryPhoto(query1, 1, 20) } returns QueryPageResult(firstQueryPhotos, 100)
        coEvery { mockPhotoDataSource.queryPhoto(query2, 1, 20) } returns QueryPageResult(secondQueryPhotos, 50)

        // Act
        useCase.loadPhotos(query1)
        useCase.loadPhotos(query2)

        // Assert
        assertEquals(secondQueryPhotos, useCase.photoFlow.first())
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query1, 1, 20) }
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query2, 1, 20) }
    }

    @Test
    fun `loadPhotos should not fetch more data when all photos are loaded`() = runTest {
        // Arrange
        val query = "nature"
        val photos = createMockPhotos(3)

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } returns QueryPageResult(photos, 3)
        // First load
        useCase.loadPhotos(query)

        // Act - attempt to load more when all photos are already loaded
        val result = useCase.loadPhotos(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow()) // Should return 0 new photos
        coVerify(exactly = 1) { mockPhotoDataSource.queryPhoto(query, 1, 20) }
    }

    @Test
    fun `loadMoreCurrentQuery should return error when current query is empty`() = runTest {
        // Act
        val result = useCase.loadMoreCurrentQuery()

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `loadMoreCurrentQuery should load more photos for current query`() = runTest {
        // Arrange
        val query = "nature"
        val firstPagePhotos = createMockPhotos(3, 1)
        val secondPagePhotos = createMockPhotos(3, 4)

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } returns QueryPageResult(firstPagePhotos, 100)
        coEvery { mockPhotoDataSource.queryPhoto(query, 2, 20) } returns QueryPageResult(secondPagePhotos, 100)

        // Act
        useCase.loadPhotos(query) // Set current query
        val result = useCase.loadMoreCurrentQuery()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow())
        assertEquals(firstPagePhotos + secondPagePhotos, useCase.photoFlow.first())
    }

    @Test
    fun `clearPhotos should reset state`() = runTest {
        // Arrange
        val query = "nature"
        val photos = createMockPhotos(3)

        coEvery { mockPhotoDataSource.queryPhoto(query, 1, 20) } returns QueryPageResult(photos, 100)
        useCase.loadPhotos(query)

        assertEquals(photos, useCase.photoFlow.first()) // Verify photos were loaded

        // Act
        useCase.clearPhotos()

        // Assert
        assertEquals(emptyList<Photo>(), useCase.photoFlow.first())

        // Verify that loadMoreCurrentQuery fails after clearing
        val result = useCase.loadMoreCurrentQuery()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    private fun createMockPhotos(count: Int, startId: Int = 1): List<Photo> {
        return List(count) { index ->
            val id = startId + index
            Photo(
                id = id.toLong(),
                width = 1000,
                height = 750,
                original = "https://www.pexels.com/photo/$id",
                photographer = "Photographer $id",
                photographerUrl = "https://www.pexels.com/photographer/$id",
                thumb = "https://images.pexels.com/photos/$id/small.jpg"
            )
        }
    }
}