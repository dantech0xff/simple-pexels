package com.creative.pexels.ui.launcher

import app.cash.turbine.test
import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.source.PhotoDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class LauncherViewModelTest {

    private lateinit var viewModel: LauncherViewModel
    private lateinit var mockDataSource: PhotoDataSource
    private val testDispatcher = StandardTestDispatcher()
    private val photoFlow = MutableStateFlow<List<Photo>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockDataSource = mockk(relaxed = true)

        // Set up the mock data source
        every { mockDataSource.photoFlow } returns photoFlow.asStateFlow()

        viewModel = LauncherViewModel(mockDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchPhotos should return flow from dataSource`() = runTest {
        // Arrange
        val mockPhotos = listOf(
            Photo(id = 1, original = "url1", photographer = "photographer1"),
            Photo(id = 2, original = "url2", photographer = "photographer2")
        )
        photoFlow.value = mockPhotos

        // Act
        val result = viewModel.searchPhotos.first()

        // Assert
        assertEquals(mockPhotos, result)
    }

    @Test
    fun `trendSearch should return empty list initially`() = runTest {
        // Act
        val result = viewModel.trendSearch.first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `querySearch should call loadPhotos on dataSource`() = runTest {
        // Arrange
        val query = "nature"
        coEvery { mockDataSource.loadPhotos(query) } returns Result.success(3)

        // Act
        viewModel.querySearch(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockDataSource.loadPhotos(query) }
    }

    @Test
    fun `loadMoreCurrentQuery should call loadMoreCurrentQuery on dataSource`() = runTest {
        // Arrange
        val mockPhotos = listOf(
            Photo(id = 1, original = "url1", photographer = "photographer1"),
            Photo(id = 2, original = "url2", photographer = "photographer2")
        )
        photoFlow.value = mockPhotos
        coEvery { mockDataSource.loadMoreCurrentQuery() } returns Result.success(2)

        // Act
        viewModel.loadMoreCurrentQuery()

        // Assert
        coVerify(exactly = 1) { mockDataSource.loadMoreCurrentQuery() }
    }

    @Test
    fun `loadMoreCurrentQuery should handle failure result`() = runTest {
        // Arrange
        val exception = IllegalStateException("Current query is empty")
        coEvery { mockDataSource.loadMoreCurrentQuery() } returns Result.failure(exception)

        // Act - Just verify no exception is thrown
        viewModel.loadMoreCurrentQuery()

        // Assert
        coVerify(exactly = 1) { mockDataSource.loadMoreCurrentQuery() }
    }

    @Test
    fun `searchPhotos should emit updates when data source flow changes`() = runTest {
        // Arrange
        val initialPhotos = listOf(
            Photo(id = 1, original = "url1", photographer = "photographer1")
        )
        val updatedPhotos = listOf(
            Photo(id = 1, original = "url1", photographer = "photographer1"),
            Photo(id = 2, original = "url2", photographer = "photographer2")
        )

        // Start with initial photos
        photoFlow.value = initialPhotos

        // Act & Assert - Verify flow updates are received
        viewModel.searchPhotos.test {
            // Initial emission
            assertEquals(initialPhotos, awaitItem())

            // Update photo flow
            photoFlow.value = updatedPhotos

            // Should receive updated photos
            assertEquals(updatedPhotos, awaitItem())

            // Cancel the flow collection
            cancelAndIgnoreRemainingEvents()
        }
    }
}