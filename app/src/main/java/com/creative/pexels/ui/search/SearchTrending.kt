package com.creative.pexels.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchTrending(
    modifier: Modifier = Modifier,
    trending: List<String>,
    onSelect: (String) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(trending.size, key = {
            it
        }, contentType = { 0 }) { index ->
            val item = trending[index]
            Button(
                onClick = {
                    onSelect(item)
                },
                modifier = Modifier
            ) {
                Text(item)
            }
        }
    }
}