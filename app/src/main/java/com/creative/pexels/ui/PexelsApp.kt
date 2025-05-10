package com.creative.pexels.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.creative.pexels.data.model.Photo
import com.creative.pexels.ui.detail.PhotoScreen
import com.creative.pexels.ui.detail.PhotoViewModel
import com.creative.pexels.ui.launcher.ILauncherViewModel
import com.creative.pexels.ui.launcher.LauncherViewModel
import com.creative.pexels.ui.search.SearchScreen
import com.creative.pexels.ui.search.SearchViewModel

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
 
@Composable
fun PexelsApp(appNavHost: NavHostController = rememberNavController()) {
    NavHost(
        navController = appNavHost,
        modifier = Modifier.fillMaxSize(),
        startDestination = NavScreen.SEARCH.value
    ) {
        composable(route = NavScreen.LAUNCHER.value) {
            val vm: ILauncherViewModel = hiltViewModel<LauncherViewModel>()
            Text(
                text = "Search",
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = NavScreen.SEARCH.value) {
            SearchScreen(hiltViewModel<SearchViewModel>(), appNavHost)
        }

        composable<Photo> { entry ->
            val photo: Photo = entry.toRoute()
            PhotoScreen(photo, hiltViewModel<PhotoViewModel>(), appNavHost)
        }
    }
}
