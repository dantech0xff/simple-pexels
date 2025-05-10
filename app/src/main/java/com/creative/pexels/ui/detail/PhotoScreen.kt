package com.creative.pexels.ui.detail

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import com.creative.pexels.R
import com.creative.pexels.data.model.Photo

/**
 * Created by dan on 10/5/25
 *
 * Copyright © 2025 1010 Creative. All rights reserved.
 */
 
@Composable
fun PhotoScreen(photo: Photo, vm: IPhotoViewModel, appNavHost: NavHostController) {
    var originalLoaded by remember { mutableStateOf(false) }
    val isFavorite by vm.isFavorite.collectAsStateWithLifecycle(false)
    val currentContext = LocalContext.current
    LaunchedEffect(Unit) {
        vm.messageSharedFlow.collect {
            Toast.makeText(currentContext, it, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.background(color = Color.Black)) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var rotation by remember { mutableFloatStateOf(0f) }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.original)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .listener(onSuccess = { _, _ ->
                    originalLoaded = true
                })
                .build(),
            contentScale = ContentScale.FillWidth,
            contentDescription = "${photo.photographer} - ${photo.id}",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotation
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotationChange ->
                        // Limit scale between 1f and 3f
                        // Handle rotation
                        rotation = (rotation + rotationChange) % 360

                        // Limit scale between 1f and 3f
                        scale = (scale * zoom).coerceIn(1f, 3f)

                        // Adjust pan based on scale
                        if (scale > 1f) {
                            // Calculate new offset based on pan and scale
                            val newOffset = Offset(
                                offset.x + pan.x * scale,
                                offset.y + pan.y * scale
                            )

                            // Calculate bounds to keep image in view
                            val maxX = (size.width * (scale - 1)) / 2
                            val maxY = (size.height * (scale - 1)) / 2

                            // Apply bounds
                            offset = Offset(
                                newOffset.x.coerceIn(-maxX, maxX),
                                newOffset.y.coerceIn(-maxY, maxY)
                            )
                        } else {
                            // Reset offset when scale is 1
                            offset = Offset.Zero
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        scale = 1F
                        rotation = 0f
                        offset = Offset.Zero
                    })
                }
        )
        AnimatedVisibility(
            originalLoaded == false, enter = fadeIn(
                tween(10, easing = LinearEasing)
            ), exit = fadeOut(tween(100, easing = LinearEasing))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.thumb)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = "${photo.photographer} - ${photo.id}",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .background(color = Color.Black.copy(alpha = 0.2f))
                .align(Alignment.TopCenter)
        ) {
            Box(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back_48px),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable(true) {
                            appNavHost.popBackStack()
                        }
                        .padding(8.dp),
                    contentDescription = null
                )
                Text(
                    "${photo.photographer} - ${photo.id}",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp).weight(1.0f)
                )
                Image(
                    painter = painterResource(
                        if (isFavorite) {
                            R.drawable.favorite_fill_48px
                        } else {
                            R.drawable.favorite_48px
                        }
                    ),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable(true) {
                            vm.toggleFavorite(photo)
                        }
                        .padding(8.dp),
                    contentDescription = null
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .background(color = Color.Black.copy(alpha = 0.2f))
                .align(Alignment.BottomCenter)
        ) {
            Row {
                Text(
                    text = "Resolution: ${photo.width} x ${photo.height}",
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp).weight(1.0f)
                )
                Image(
                    painter = painterResource(R.drawable.download_48px),
                    modifier = Modifier
                        .size(42.dp)
                        .clickable(true) {
                            vm.download(photo)
                        }
                        .padding(8.dp),
                    contentDescription = null
                )
            }
            Box(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}
