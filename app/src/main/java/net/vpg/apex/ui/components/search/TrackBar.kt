package net.vpg.apex.ui.components.search

import android.R.attr.onClick
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vpg.apex.di.rememberPlayer
import net.vpg.apex.player.ApexTrack

@Composable
fun TrackBar(apexTrack: ApexTrack, onClick: () -> Unit = { }) {
    val player = rememberPlayer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { player.play(apexTrack); onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = apexTrack.name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = apexTrack.category,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}