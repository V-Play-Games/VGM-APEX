package net.vpg.apex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

object LibraryScreen : ApexBottomBarScreen(
    route = "library",
    icon = Icons.Default.LibraryMusic,
    title = "Library",
    screen = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text("Your Library", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 24.sp)
        }
    }
)
