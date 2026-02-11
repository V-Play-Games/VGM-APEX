package net.vpg.apex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.vpg.apex.core.LocalNavigationManager
import net.vpg.apex.core.auth.AuthManager
import net.vpg.apex.core.auth.AuthState
import net.vpg.apex.core.di.rememberContext
import net.vpg.apex.core.rememberNavigationManager
import net.vpg.apex.entities.ApexAlbum
import net.vpg.apex.entities.ApexTrack
import net.vpg.apex.entities.ApexUploader
import net.vpg.apex.ui.components.navigation.MainScaffold
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
        val authState by AuthManager.authState.collectAsState()
        if (authState !is AuthState.Authenticated) {
            AuthScreen()
            return
        }

        CompositionLocalProvider(LocalNavigationManager provides rememberNavigationManager(HomeRoute)) {
            MainScaffold(
                HomeScreen,
                SearchScreen,
                LibraryScreen,
                NowPlayingScreen,
                TrackInfoScreen,
                AlbumInfoScreen,
                SettingsScreen,
                ProfileScreen
            )
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
