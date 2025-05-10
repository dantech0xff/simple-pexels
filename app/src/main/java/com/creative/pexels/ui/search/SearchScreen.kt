package com.creative.pexels.ui.search

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.creative.pexels.ui.PhotoResults

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */

internal const val DefaultAnimationDuration = 300

@Composable
fun SearchScreen(vm: ISearchViewModel, appNavHost: NavHostController) {
    val currentContext = LocalContext.current
    LaunchedEffect(Unit) {
        vm.messageSharedFlow.collect {
            Toast.makeText(currentContext, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold { paddingValues ->
        AnimatedVisibility(true, modifier = Modifier.padding(paddingValues)) {
            val uiState by vm.uiState.collectAsStateWithLifecycle(SearchScreenUiState.empty())
            Column(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    uiState.searchKeyword,
                    updateQuery = {
                        vm.setSearchKeyword(it)
                    }
                ) { query ->
                    vm.querySearch(query)
                }
                AnimatedVisibility(
                    uiState.isLoading,
                    enter = fadeIn(tween(DefaultAnimationDuration, easing = LinearEasing))
                            + expandVertically(tween(DefaultAnimationDuration, easing = LinearEasing)),
                    exit = fadeOut(tween(DefaultAnimationDuration, easing = LinearEasing))
                            + shrinkVertically(tween(DefaultAnimationDuration, easing = LinearEasing))
                ) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), strokeCap = StrokeCap.Square)
                }
                AnimatedVisibility(
                    uiState.trendKeywords.isNotEmpty(),
                    enter = fadeIn(tween(DefaultAnimationDuration, easing = LinearEasing)),
                    exit = fadeOut(tween(DefaultAnimationDuration, easing = LinearEasing))
                ) {
                    SearchTrending(modifier = Modifier.fillMaxWidth().wrapContentHeight(), trending = uiState.trendKeywords) {
                        vm.querySearch(it)
                    }
                }
                AnimatedVisibility(
                    uiState.searchPhotos.isNotEmpty(),
                    enter = fadeIn(tween(DefaultAnimationDuration, easing = LinearEasing)),
                    exit = fadeOut(tween(DefaultAnimationDuration, easing = LinearEasing))
                ) {
                    PhotoResults(modifier = Modifier.fillMaxSize(), uiState.searchPhotos, vm) {
                        appNavHost.navigate(it)
                    }
                }
                AnimatedVisibility(
                    uiState.searchPhotos.isEmpty(),
                    enter = fadeIn(tween(DefaultAnimationDuration, easing = LinearEasing)),
                    exit = fadeOut(tween(DefaultAnimationDuration, easing = LinearEasing)),
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp)
                ) {
                    SearchEmpty(uiState.emptyPlaceHolderUiState)
                }
            }
        }
    }
}
