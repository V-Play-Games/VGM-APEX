package net.vpg.apex

import BottomNavigationBar
import TopBar
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.vpg.apex.ui.components.home.EditorsPicksSection
import net.vpg.apex.ui.components.home.NowPlayingBar
import net.vpg.apex.ui.components.home.RecentlyPlayedSection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApexTheme {
                SplashScreenWithCrossfade()
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Composable
    fun SplashScreenWithCrossfade() {
        // State to control which screen to show
        var showSplash by remember { mutableStateOf(true) }

        // Implement the crossfade transition
        Crossfade(
            targetState = showSplash,
            animationSpec = tween(durationMillis = 800),
            label = "splash_transition"
        ) { isShowingSplash ->
            if (isShowingSplash) {
                SplashScreen()
            } else {
                MainContent()
            }
        }

        // Trigger the transition after a delay
        LaunchedEffect(true) {
            delay(500)
            showSplash = false
        }
    }

    @Composable
    fun SplashScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pika_chill),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
        }
    }

    @Composable
    fun MainContent() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Scaffold(
                // Set a custom top bar that accounts for the status bar height
                modifier = Modifier.statusBarsPadding(),
                topBar = { TopBar() },
                bottomBar = {
                    Column {
                        NowPlayingBar()
                        BottomNavigationBar()
                    }
                },
                // Use transparent container color for proper window insets handling
                containerColor = MaterialTheme.colorScheme.background,
                // Explicitly set content window insets to empty since we're handling them manually
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                // Main content of the app
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Add your main content here
                    EditorsPicksSection()
                    RecentlyPlayedSection()
                }
            }
        }
    }
}