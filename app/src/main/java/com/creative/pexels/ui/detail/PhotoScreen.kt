package com.creative.pexels.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import com.creative.pexels.R
import com.creative.pexels.data.model.Photo
import com.creative.pexels.ui.launcher.ILauncherViewModel

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
 
@Composable
fun PhotoScreen(id: Long, vm: ILauncherViewModel, appNavHost: NavHostController) {
    var photo: Photo? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        vm.searchPhotos.collect {
            photo = it.find { it.id == id }
        }
    }

    Box(modifier = Modifier.background(color = Color.Black)) {
        Column(modifier = Modifier.background(color = Color.Black.copy(alpha = 0.2f)).align(Alignment.TopCenter)) {
            Box(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.close_48px),
                    modifier = Modifier.size(42.dp).clickable(true) {
                        appNavHost.popBackStack()
                    }.align(Alignment.CenterEnd),
                    contentDescription = null
                )
            }
        }
        photo?.let {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it.original)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = "${it.photographer} - ${it.id}",
                modifier = Modifier.fillMaxSize().align(Alignment.Center)
            )
        }
    }
}