package net.vplaygames.apex

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
import net.vplaygames.apex.ui.components.navigation.MainScaffold
import net.vplaygames.apex.ui.screens.*

@AndroidEntryPoint
class ApexActivity : ComponentActivity() {
    private var isReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !isReady }
        super.onCreate(savedInstanceState)
        setContent {
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
        LaunchedEffect(Unit) {
            ApiClient.login()
                .onSuccess { isReady = true }
                .onFailure {
                    Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
                    Log.e("ApexActivity", "Login failed", it)
                    AuthManager.signOut()
                }
        }
        if (!isReady) return

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
}
