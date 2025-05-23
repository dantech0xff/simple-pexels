package com.creative.pexels.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import com.creative.pexels.R
import com.creative.pexels.data.model.Photo
import com.creative.pexels.ui.search.ISearchViewModel

private const val LOAD_MORE_OFFSET = 5

@Composable
fun PhotoResults(modifier: Modifier, photoList: List<Photo>, vm: ISearchViewModel, onSelect: (Photo) -> Unit) {
    val lazyGridState = rememberLazyGridState()
    LaunchedEffect(Unit) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo }.collect {
            val totalSize = lazyGridState.layoutInfo.totalItemsCount
            if (totalSize > 0 && it.isNotEmpty() && it.last().index >= totalSize - LOAD_MORE_OFFSET) {
                vm.loadMoreCurrentQuery()
            }
        }
    }

    LazyVerticalGrid(GridCells.Fixed(2), modifier = modifier, state = lazyGridState) {
        items(photoList.size, key = { index -> index }, span = null, contentType = { photoList[it]::class::simpleName }) {
            val photo = photoList[it]
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.thumb)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "${photo.photographer} - ${photo.id}",
                modifier = Modifier.height(220.dp).padding(2.dp).clip(RoundedCornerShape(12.dp)).clickable(true) {
                    onSelect.invoke(photo)
                }
            )
        }
    }
}