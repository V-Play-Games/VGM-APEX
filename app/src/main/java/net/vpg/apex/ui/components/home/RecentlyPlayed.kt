package net.vpg.apex.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecentlyPlayedSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Recently played", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(
                listOf(
                    "OneDirection",
                    "Lana Del Rey",
                    "Marvin Gaye",
                    "A very long name which should cause problems"
                )
            ) { title ->
                AlbumCard(title)
            }
        }
    }
}

@Composable
fun AlbumCard(title: String) {
    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 12.sp,
            maxLines = 2,                        // Allow up to 2 lines of text
            overflow = TextOverflow.Ellipsis     // Add ellipsis (...) for text that's too long
        )
    }
}