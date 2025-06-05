package net.vpg.apex.ui.screens

import ApexBottomBarScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import net.vpg.apex.di.rememberSearchHistory
import net.vpg.apex.player.ApexTrack
import net.vpg.apex.ui.components.search.CurrentSearchItem
import net.vpg.apex.ui.components.search.RecentSearchItem

object SearchScreen : ApexBottomBarScreen(
    route = "search",
    icon = Icons.Default.Search,
    title = "Search",
    screen = {
        // State management
        var searchQuery by rememberSaveable { mutableStateOf("") }
        val searchHistory = rememberSearchHistory()

        // Store search results
        val searchResults = remember(searchQuery) {
            if (searchQuery.isEmpty()) {
                emptyList()
            } else {
                searchTracks(searchQuery)
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
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
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = Color.Gray
                            )
                        }
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
                            CurrentSearchItem(track)
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

                LazyColumn {
                    items(searchHistory.getTracks()) { track ->
                        RecentSearchItem(track)
                    }
                }
            }
        }
    }
)

fun searchTracks(query: String) = if (query.isEmpty())
    emptyList()
else
    ApexTrack.TRACKS_DB.values.filter { it.title.contains(query, ignoreCase = true) }