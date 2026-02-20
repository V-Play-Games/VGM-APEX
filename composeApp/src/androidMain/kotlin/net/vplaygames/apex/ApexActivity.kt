package net.vplaygames.apex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.vplaygames.apex.core.LocalNavigationManager
import net.vplaygames.apex.core.api.ApiClient
import net.vplaygames.apex.core.auth.AuthManager
import net.vplaygames.apex.core.auth.AuthState
import net.vplaygames.apex.core.di.rememberContext
import net.vplaygames.apex.core.rememberNavigationManager
import net.vplaygames.apex.entities.ApexAlbum
import net.vplaygames.apex.entities.ApexTrack
import net.vplaygames.apex.entities.ApexUploader
import net.vplaygames.apex.ui.components.navigation.MainScaffold
import net.vplaygames.apex.ui.screens.*
import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONObject
import net.vplaygames.apex.ui.screens.SearchScreen

@AndroidEntryPoint
class ApexActivity : ComponentActivity() {
    private var isReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !isReady }
        super.onCreate(savedInstanceState)
        setContent {
            loadData(rememberContext())
            ApexTheme {
                MainContent()
            }
        }
        startService(Intent(this, ApexNotificationService::class.java))
    }

    @Composable
    fun MainContent() {
        val context = rememberContext()
        val authState by AuthManager.authState.collectAsState()
        if (authState !is AuthState.Authenticated) {
            isReady = true
            AuthScreen()
            return
        }
        var isLoggedIn by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            ApiClient.login()
                .onSuccess { isLoggedIn = true }
                .onFailure {
                    Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
                    Log.e("ApexActivity", "Login failed", it)
                    AuthManager.signOut()
                }
        }
        isReady = isLoggedIn
        if (!isLoggedIn) return

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
