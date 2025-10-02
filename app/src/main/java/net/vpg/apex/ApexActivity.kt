package net.vpg.apex

import android.content.Intent
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
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.core.DataLoader
import net.vpg.apex.core.di.rememberContext
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.ui.components.navigation.BottomBar
import net.vpg.apex.ui.components.navigation.TopBar
import net.vpg.apex.ui.components.player.NowPlayingBar
import net.vpg.apex.ui.components.player.SeekBar
import net.vpg.apex.ui.screens.*

@AndroidEntryPoint
class ApexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApexTheme {
                SplashScreenWithCrossfade()
            }
        }
        startService(Intent(this, ApexNotificationService::class.java))
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
                    .fillMaxSize() // don't remove
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
        val navControllerProvider = rememberNavControllerProvider()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        CompositionLocalProvider(navControllerProvider provides navController) {
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                topBar = { TopBar() },
                bottomBar = {
                    Column {
                        // Only show the NowPlayingBar if not on the NowPlayingScreen
                        AnimatedVisibility(currentRoute != NowPlayingScreen.route) {
                            Box {
                                NowPlayingBar()
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .zIndex(1f)
                                        .offset(y = 20.dp)
                                ) {
                                    SeekBar(bottomBar = true)
                                }
                            }
                        }
                        BottomBar()
                    }
                },
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = HomeScreen.route,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    listOf(
                        HomeScreen,
                        SearchScreen,
                        LibraryScreen,
                        NowPlayingScreen,
                        TrackInfoScreen,
                        AlbumInfoScreen,
                        SettingsScreen
                    ).forEach {
                        it.composeTo(this, navController)
                    }
                }
            }
        }
    }
}
