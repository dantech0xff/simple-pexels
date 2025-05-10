package com.creative.pexels.ui.search

import app.cash.turbine.test
import com.creative.pexels.data.model.Photo
import com.creative.pexels.data.source.PhotoDataSource
import com.creative.pexels.dispatchers.AppDispatchers
import com.creative.pexels.dispatchers.AppDispatchersTestImpl
import com.creative.pexels.usecase.QueryPhotosByKeywordUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    private lateinit var queryPhotoUseCase: QueryPhotosByKeywordUseCase
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var photoDataSource: PhotoDataSource
    private lateinit var appDispatchers: AppDispatchers
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun start() {
        Dispatchers.setMain(testDispatcher)
        photoDataSource = mockk(relaxed = true, relaxUnitFun = true)
        appDispatchers = AppDispatchersTestImpl()
        queryPhotoUseCase = spyk(QueryPhotosByKeywordUseCase(photoDataSource, appDispatchers))
        searchViewModel = SearchViewModel(queryPhotoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `setSearchKeyword should update the search keyword state`() = runTest {
        // Given
        val testKeyword = "nature"

        // When
        searchViewModel.setSearchKeyword(testKeyword)

        // Then
        searchViewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testKeyword, state.searchKeyword)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `querySearch should update loading state and call loadPhotos`() = runTest {
        // Given
        val testQuery = "mountain"
        coEvery { queryPhotoUseCase.loadPhotos(any()) } returns Result.success(10)

        // When
        searchViewModel.querySearch(testQuery)
        advanceUntilIdle()

        // Then
        coVerify { queryPhotoUseCase.loadPhotos(testQuery) }

        searchViewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(testQuery, initialState.searchKeyword)
            assertFalse(initialState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMoreCurrentQuery should update loading state and call use case method`() = runTest {
        // Given
        coEvery { queryPhotoUseCase.loadMoreCurrentQuery() } returns Result.success(5)

        // When
        searchViewModel.loadMoreCurrentQuery()
        advanceUntilIdle()

        // Then
        coVerify { queryPhotoUseCase.loadMoreCurrentQuery() }

        searchViewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState should combine all state flows correctly`() = runTest {
        // Given
        val testPhotos = listOf(
            Photo(id = 1, width = 100, height = 100,
                photographer = "",
                photographerUrl = "",
                original = "original",
                thumb = "thumb"
                )
        )
        val testQuery = "buildings"
        val emptyState = EmptyPlaceHolderUiState(text = "Test", lottieResId = 123)

        // Set up our spyk to emit the test data
        coEvery { queryPhotoUseCase.photoFlow } returns kotlinx.coroutines.flow.MutableStateFlow(testPhotos)
        coEvery { queryPhotoUseCase.emptyStateFlow } returns kotlinx.coroutines.flow.MutableStateFlow(emptyState)

        // When
        searchViewModel.setSearchKeyword(testQuery)
        advanceUntilIdle()

        // Then
        searchViewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testPhotos, state.searchPhotos)
            assertEquals(testQuery, state.searchKeyword)
            assertEquals(emptyState, state.emptyPlaceHolderUiState)
            assertTrue(state.trendKeywords.isNotEmpty())
            assertEquals(null, state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `querySearch should set isLoading to true then false`() = runTest {
        // Given
        val testQuery = "beach"
        val loadingStates = mutableListOf<Boolean>()

        coEvery { queryPhotoUseCase.loadPhotos(any()) } coAnswers {
            // Capture loading state during execution
            searchViewModel.uiState.test {
                loadingStates.add(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
            Result.success(10)
        }

        // When
        searchViewModel.uiState.test {
            // Initial state
            awaitItem()

            // Execute query
            searchViewModel.querySearch(testQuery)
            advanceUntilIdle()

            // Final state
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        assertTrue("Loading state should be true during execution", loadingStates.contains(true))
    }

    @Test
    fun `loadMoreCurrentQuery should set isLoading to true then false`() = runTest {
        // Given
        val loadingStates = mutableListOf<Boolean>()

        coEvery { queryPhotoUseCase.loadMoreCurrentQuery() } coAnswers {
            // Capture loading state during execution
            searchViewModel.uiState.test {
                loadingStates.add(awaitItem().isLoading)
                cancelAndIgnoreRemainingEvents()
            }
            Result.success(5)
        }

        // When
        searchViewModel.uiState.test {
            // Initial state
            awaitItem()

            // Execute query
            searchViewModel.loadMoreCurrentQuery()
            advanceUntilIdle()

            // Final state
            assertTrue(awaitItem().isLoading)
            assertFalse(awaitItem().isLoading)
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        assertTrue(
            "Loading state should be true during execution",
            loadingStates.contains(true)
        )
    }

    @Test
    fun `initial trending keywords should be populated`() = runTest {
        // When/Then
        searchViewModel.uiState.test {
            val state = awaitItem()
            val expectedKeywords = listOf(
                "bitcoin", "flower", "american",
                "espresso", "trump", "to the moon"
            )
            assertEquals(expectedKeywords, state.trendKeywords)
            cancelAndIgnoreRemainingEvents()
        }
    }
}