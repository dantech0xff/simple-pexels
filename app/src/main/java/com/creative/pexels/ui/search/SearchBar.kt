package com.creative.pexels.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.creative.pexels.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    updateQuery: (String) -> Unit = {},
    onSearch: (String) -> Unit
) {
    Row(
        modifier = modifier.height(58.dp),
        horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { query ->
                updateQuery.invoke(query)
            },
            modifier = Modifier.fillMaxHeight().weight(1f),
            placeholder = {
                Text("Type Search Keyword")
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RectangleShape,
            trailingIcon = {
                Image(
                    painter = painterResource(R.drawable.close_48px),
                    contentDescription = "Clear Icon",
                    modifier = Modifier.clickable {
                        updateQuery.invoke("")
                    }.size(24.dp)
                )
            },
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedContainerColor = Color.White.copy(alpha = 0.18f),
                focusedContainerColor = Color.White.copy(alpha = 0.18f)
            )
        )
        Box(
            modifier = Modifier.clickable {
                onSearch(searchQuery)
            }.padding(horizontal = 16.dp).fillMaxHeight()
        ) {
            Image(
                painter = painterResource(R.drawable.search_48px),
                contentDescription = "Search Icon",
                modifier = Modifier.size(32.dp).align(Alignment.Center)
            )
        }
    }
}
