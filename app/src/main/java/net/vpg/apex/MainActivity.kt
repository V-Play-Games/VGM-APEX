package net.vpg.apex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.core.DataLoader
import net.vpg.apex.core.di.rememberContext
import net.vpg.apex.ui.components.navigation.BottomBar
import net.vpg.apex.ui.components.navigation.TopBar
import net.vpg.apex.ui.components.player.NowPlayingBar
import net.vpg.apex.ui.screens.HomeScreen
import net.vpg.apex.ui.screens.LibraryScreen
import net.vpg.apex.ui.screens.NowPlayingScreen
import net.vpg.apex.ui.screens.SearchScreen

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
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = { TopBar() },
            bottomBar = {
                Column {
                    // Only show the NowPlayingBar if not on the NowPlayingScreen
                    AnimatedVisibility(currentRoute != NowPlayingScreen.route) {
                        NowPlayingBar(navController)
                    }
                    BottomBar(navController)
                }
            },
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = HomeScreen.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                HomeScreen.composeTo(this)
                SearchScreen.composeTo(this)
                LibraryScreen.composeTo(this)
                NowPlayingScreen.composeTo(this)
            }
        }
    }
}
