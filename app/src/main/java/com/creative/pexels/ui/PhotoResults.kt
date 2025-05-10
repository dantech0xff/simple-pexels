package com.creative.pexels.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.creative.pexels.data.model.Photo

@Composable
fun PhotoResults(modifier: Modifier, photoList: List<Photo>, onSelect: (Photo) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(2), modifier = modifier) {
        items(photoList.size, key = { index -> index }, span = null, contentType = { photoList[it]::class::simpleName }) {
            val photo = photoList[it]
            Text(
                photo.original
            )
        }
    }
}