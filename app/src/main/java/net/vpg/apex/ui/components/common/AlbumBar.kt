package net.vpg.apex.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.core.bounceClick
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.entities.ApexAlbum

@Composable
fun AlbumBar(apexAlbum: ApexAlbum) {
    val navController = rememberNavControllerProvider().current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth() // don't remove
            .padding(vertical = 8.dp)
            .bounceClick { navController.navigate(apexAlbum) }
    ) {
        AlbumImage(apexAlbum, size = 75, cornerRadius = 0)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = apexAlbum.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = apexAlbum.tracks
                    .groupBy { it.uploader.name }
                    .mapValues { it.value.size }
                    .toList()
                    .groupBy { it.second }
                    .toSortedMap(Comparator.reverseOrder())
                    .values
                    .flatten()
                    .joinToString(", ") { it.first },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
