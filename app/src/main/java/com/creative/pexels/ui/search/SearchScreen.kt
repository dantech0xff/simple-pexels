package com.creative.pexels.ui.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.creative.pexels.ui.PhotoResults
import com.creative.pexels.ui.launcher.ILauncherViewModel
import java.security.SecureRandom

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
 
@Composable
fun SearchScreen(vm: ILauncherViewModel, appNavHost: NavHostController) {
    Scaffold { paddingValues ->
        AnimatedVisibility(true, modifier = Modifier.padding(paddingValues)) {
            var mutableSearchQueryState by remember { mutableStateOf("") }
            val photoList by vm.searchPhotos.collectAsStateWithLifecycle(emptyList())
            val trendingList by vm.trendSearch.collectAsStateWithLifecycle(emptyList())
            Column(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    mutableSearchQueryState,
                    updateQuery = {
                        mutableSearchQueryState = it
                    }
                ) { query ->
                    vm.querySearch(query)
                }
                AnimatedVisibility(trendingList.isNotEmpty(), enter = fadeIn(tween(200, easing = LinearEasing)), exit = fadeOut(tween(200, easing = LinearEasing))) {
                    SearchTrending(modifier = Modifier.fillMaxWidth().wrapContentHeight(), trending = trendingList) {
                        mutableSearchQueryState = it
                    }
                }
                AnimatedVisibility(photoList.isNotEmpty(), enter = fadeIn(tween(200, easing = LinearEasing)), exit = fadeOut(tween(200, easing = LinearEasing))) {
                    PhotoResults(modifier = Modifier.fillMaxSize(), photoList, vm) {
                        Log.d("SearchScreen", "onClick: $it")
                    }
                }
            }
        }
    }
}

