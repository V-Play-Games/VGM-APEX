package net.vpg.apex

import MusicAppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.di.rememberContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApexTheme {
                SplashScreenWithCrossfade()
            }
        }
    }

    @Composable
    fun SplashScreenWithCrossfade() {
        // State to control which screen to show
        var showSplash by remember { mutableStateOf(true) }

        // Implement the crossfade transition
        Crossfade(
            targetState = showSplash,
            animationSpec = tween(durationMillis = 800)
        ) { isShowingSplash ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                if (isShowingSplash) {
                    SplashScreen()
                } else {
                    MainContent()
                }
            }
        }

        // Trigger the transition after a delay
        val context = rememberContext()
        LaunchedEffect(Unit) {
            DataLoader.loadData(context)
            showSplash = false
        }
    }

    @Composable
    fun SplashScreen() {
        Image(
            painter = painterResource(id = R.drawable.ic_pika_chill),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )
    }

    @Composable
    fun MainContent() {
        MusicAppNavigation()
    }
}
