package net.vpg.apex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SplashScreenWithCrossfade()
            }
        }

    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Composable
    fun AppTheme(content: @Composable () -> Unit) {
        MaterialTheme {
            content()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            Greeting("Android")
        }
    }


    @Composable
    fun SplashScreenWithCrossfade() {
        // State to control which screen to show
        var showSplash by remember { mutableStateOf(true) }

        // Implement the crossfade transition
        Crossfade(
            targetState = showSplash,
            animationSpec = tween(durationMillis = 800), // 800ms is a good duration for crossfade
            label = "splash_transition"
        ) { isShowingSplash ->
            if (isShowingSplash) {
                SplashScreen()
            } else {
                MainContent()
            }
        }

        // Trigger the transition after 2 seconds
        LaunchedEffect(key1 = true) {
            delay(500) // Show splash for 2 seconds
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
                painter = painterResource(id = R.drawable.ic_splash),
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
            Greeting("Android")
        }
    }

}