package net.vpg.apex.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditorsPicksSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Editor's picks", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow {
            items(listOf("Pick 1", "Pick 2", "Pick 3")) { title ->
                EditorsPickCard(title)
            }
        }
    }
}

@Composable
fun EditorsPickCard(title: String) {
    Column(
        modifier = Modifier
            .padding(end = 12.dp)
            .width(150.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
    }
}