package net.vpg.apex.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.ui.components.common.AlbumBar

object LibraryScreen : ApexBottomBarScreen(
    route = "library",
    icon = Icons.Default.LibraryMusic,
    title = "Library",
    columnModifier = Modifier.padding(horizontal = 12.dp),
    content = {
        Text(
            text = "APEX Library",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.padding(4.dp))
        LazyColumn {
            items(ApexAlbum.ALBUMS_DB.values.toList()) { album ->
                if (album != ApexAlbum.EMPTY)
                    AlbumBar(album)
            }
        }
    }
)
