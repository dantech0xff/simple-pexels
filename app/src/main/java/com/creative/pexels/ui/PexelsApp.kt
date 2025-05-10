package com.creative.pexels.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.creative.pexels.ui.launcher.ILauncherViewModel

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
 
@Composable
fun PexelsApp(vm: ILauncherViewModel, appNavHost: NavHostController = rememberNavController()) {
    NavHost(
        navController = appNavHost,
        modifier = Modifier.fillMaxSize(),
        startDestination = "launcher"
    ) {
        composable(route = "launcher") {
            Text("This is the launcher screen!!!!")
        }
    }
}
