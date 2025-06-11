package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberSearchHistory
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.ui.components.common.TrackBar

object SearchScreen : ApexBottomBarScreen(
    route = "search",
    icon = Icons.Default.Search,
    title = "Search",
    columnModifier = Modifier.padding(horizontal = 12.dp),
    content = {
        // State management
        var searchQuery by rememberSaveable { mutableStateOf("") }
        val searchHistory = rememberSearchHistory()

        // Store search results
        val searchResults = remember(searchQuery) {
            if (searchQuery.isEmpty())
                emptyList()
            else
                ApexTrack.TRACKS_DB.values.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), // don't remove
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.LightGray,
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        modifier = Modifier.bounceClick { searchQuery = "" },
                        contentDescription = "Clear Search",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Display either search results or recent searches
        if (searchQuery.isNotEmpty()) {
            Text(
                text = "Search Results",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                Text(
                    text = "No results found for \"$searchQuery\"",
                    color = Color.Gray
                )
            } else {
                LazyColumn {
                    items(searchResults) { track ->
                        TrackBar(
                            apexTrack = track,
                            onClick = { searchHistory.addTrack(track) }
                        )
                    }
                }
            }
        } else {
            Text(
                text = "Recent searches",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            searchHistory.ComposeToList(
                emptyFallback = {
                    Text(
                        text = "No recent searches",
                        color = Color.Gray
                    )
                },
                lazyComposable = { LazyColumn(content = it) },
                content = { track, index ->
                    TrackBar(
                        apexTrack = track,
                        trailingComponents = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(40.dp)
                                    .bounceClick { searchHistory.removeIndex(index) },
                                tint = Color.DarkGray
                            )
                        }
                    )
                },
            )
        }
    }
)