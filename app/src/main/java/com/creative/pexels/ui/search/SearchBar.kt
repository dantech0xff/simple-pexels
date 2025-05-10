package com.creative.pexels.ui.search

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    updateQuery: (String) -> Unit = {},
    onSearch: (String) -> Unit
) {
    Row(modifier = modifier) {
        TextField(
            value = searchQuery,
            onValueChange = { query ->
                updateQuery.invoke(query)
            },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = {
                Text("Search")
            }
        )
        Button(
            onClick = {
                onSearch(searchQuery)
            },
            modifier = Modifier
        ) {
            Text("Search")
        }
    }
}