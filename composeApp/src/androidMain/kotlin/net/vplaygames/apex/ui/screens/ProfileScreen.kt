package net.vplaygames.apex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import net.vplaygames.apex.core.auth.AuthManager
import net.vplaygames.apex.core.auth.AuthState
import net.vplaygames.apex.util.customShimmer

@Serializable
object ProfileRoute : NavKey

object ProfileScreen : ApexScreenStatic<ProfileRoute>(
    route = ProfileRoute,
    columnModifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    content = {
        val authState = AuthManager.authState.collectAsState().value
        val context = LocalContext.current

        if (authState !is AuthState.Authenticated) {
            // should not happen since we check for authentication in ApexActivity, but just in case
            error("Invalid state")
        }

        Spacer(modifier = Modifier.height(32.dp))

        val user = authState.user

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(user.photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Pic",
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.customShimmer(durationMillis = 800, delayMillis = 200),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .alpha(0.5f)
                    )
                }
            },
            error = {
                it.result.throwable.printStackTrace()
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .alpha(0.5f)
                    )
                }
            },
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(120.dp)),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display Name
        Text(
            text = user.displayName ?: "User",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        user.email?.let { email ->
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Sign Out Button
        OutlinedButton(
            onClick = { AuthManager.signOut() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Sign Out",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out")
        }
    }
)
