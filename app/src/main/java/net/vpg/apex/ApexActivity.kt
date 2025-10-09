package net.vpg.apex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.core.di.rememberContext
import net.vpg.apex.core.di.rememberNavControllerProvider
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexUploader
import net.vpg.apex.ui.components.navigation.BottomBar
import net.vpg.apex.ui.components.navigation.TopBar
import net.vpg.apex.ui.screens.*
import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONObject

@AndroidEntryPoint
class ApexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var isDataLoaded by mutableStateOf(false)
        installSplashScreen().setKeepOnScreenCondition { !isDataLoaded }
        super.onCreate(savedInstanceState)
        setContent {
            loadData(rememberContext())
            isDataLoaded = true
            ApexTheme {
                MainContent()
            }
        }
        startService(Intent(this, ApexNotificationService::class.java))
    }

    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        val navControllerProvider = rememberNavControllerProvider()

        CompositionLocalProvider(navControllerProvider provides navController) {
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                topBar = { TopBar() },
                bottomBar = { BottomBar() },
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

    fun loadData(context: Context) {
        mapOf<String, (JSONObject) -> Unit>(
            "tracks" to { ApexTrack(it) },
            "albums" to { ApexAlbum(it) },
            "uploaders" to { ApexUploader(it) }
        ).forEach { (type, constructor) ->
            context.assets
                .open("$type.json")
                .toJSON()
                .toArray()
                .forEach { constructor(it.toObject()) }
        }
    }
}
