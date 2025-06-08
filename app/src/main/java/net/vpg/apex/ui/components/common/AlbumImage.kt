package net.vpg.apex.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import net.vpg.apex.core.di.rememberContext
import net.vpg.apex.entities.ApexAlbum

@Composable
fun AlbumImage(album: ApexAlbum, size: Int, cornerRadius: Int = 8) {
    val context = rememberContext()
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(album.albumArtUrl)
            .crossfade(true)
            .build(),
        contentDescription = "${album.name} cover",
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size((size * 2 / 3).dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        error = {
            it.result.throwable.printStackTrace()
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "App Logo",
                    modifier = Modifier.size((size * 2 / 3).dp).alpha(0.5f)
                )
            }
        },
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(cornerRadius.dp)),
    )
}

@Composable
fun AlbumImageWithInfoButton(album: ApexAlbum, size: Int, cornerRadius: Int = 8, onClick: () -> Unit = {}) {
    Box {
        AlbumImage(album, size, cornerRadius)
        Icon(
            Icons.Outlined.Info,
            contentDescription = "Star",
            modifier = Modifier
                .shadow(elevation = 4.dp)
                .align(Alignment.TopEnd)
                .zIndex(1f)
                .clickable { onClick() }
        )
    }
}
