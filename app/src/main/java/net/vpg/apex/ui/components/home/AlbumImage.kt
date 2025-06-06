package net.vpg.apex.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import net.vpg.apex.R
import net.vpg.apex.di.rememberContext
import net.vpg.apex.player.ApexAlbum

@Composable
fun AlbumImage(album: ApexAlbum, size: Int) {
    val context = rememberContext()
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(album.albumArtUrl)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .build(),
        contentDescription = "${album.name} cover",
        contentScale = ContentScale.Crop,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_song_broken),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(size.dp)
                )
            }
        },
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}