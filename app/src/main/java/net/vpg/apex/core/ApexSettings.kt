package net.vpg.apex.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Generic extension function for collecting enum settings with first value as default
@Composable
inline fun <reified T> Flow<T>.asStateValue() where T : Enum<T>, T : ApexSetting =
    collectAsState(initial = enumValues<T>().first()).value

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apex_settings")

class ApexSettings(context: Context) {
    private val dataStore = context.dataStore
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val theme = dataStore.data.map { preferences ->
        try {
            ThemeMode.valueOf(preferences[PreferenceKeys.THEME]!!)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }
    val accentColor = dataStore.data.map { preferences ->
        try {
            AccentColor.valueOf(preferences[PreferenceKeys.ACCENT_COLOR]!!)
        } catch (_: Exception) {
            AccentColor.GREEN
        }
    }
    val animationSpeed = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ANIMATION_SPEED] ?: 1.0f
    }
    val marqueeSpeed = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.MARQUEE_SPEED] ?: 1.0f
    }
    val gridSize = dataStore.data.map { preferences ->
        try {
            GridSize.valueOf(preferences[PreferenceKeys.GRID_SIZE]!!)
        } catch (_: Exception) {
            GridSize.MEDIUM
        }
    }
    val historyRetention = dataStore.data.map { preferences ->
        try {
            HistoryRetention.valueOf(preferences[PreferenceKeys.HISTORY_RETENTION]!!)
        } catch (_: Exception) {
            HistoryRetention.THIRTY_DAYS
        }
    }

    object PreferenceKeys {
        val THEME = stringPreferencesKey("theme")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val ANIMATION_SPEED = floatPreferencesKey("animation_speed")
        val MARQUEE_SPEED = floatPreferencesKey("marquee_speed")
        val GRID_SIZE = stringPreferencesKey("grid_size")
        val HISTORY_RETENTION = stringPreferencesKey("history_retention")
    }

    fun updateTheme(theme: ThemeMode) = updatePreference(PreferenceKeys.THEME, theme.name)

    fun updateAccentColor(color: AccentColor) = updatePreference(PreferenceKeys.ACCENT_COLOR, color.name)

    fun updateAnimationSpeed(speed: Float) = updatePreference(PreferenceKeys.ANIMATION_SPEED, speed)

    fun updateMarqueeSpeed(speed: Float) = updatePreference(PreferenceKeys.MARQUEE_SPEED, speed)

    fun updateGridSize(size: GridSize) = updatePreference(PreferenceKeys.GRID_SIZE, size.name)

    fun updateHistoryRetention(retention: HistoryRetention) =
        updatePreference(PreferenceKeys.HISTORY_RETENTION, retention.name)

    fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
}

interface ApexSetting {
    val displayName: String
}

enum class ThemeMode(override val displayName: String) : ApexSetting {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AccentColor(
    override val displayName: String,
    val primary: Long,
    val light: Long,
    val dark: Long
) : ApexSetting {
    GREEN("Green", 0xFF1DB954, 0xFF1ED760, 0xFF1AA34A),
    BLUE("Blue", 0xFF1976D2, 0xFF42A5F5, 0xFF0D47A1),
    PURPLE("Purple", 0xFF7B1FA2, 0xFFAB47BC, 0xFF4A148C),
    ORANGE("Orange", 0xFFFF9800, 0xFFFFB74D, 0xFFE65100),
    RED("Red", 0xFFD32F2F, 0xFFEF5350, 0xFFB71C1C)
}

enum class GridSize(override val displayName: String) : ApexSetting {
    COMPACT("Compact"),
    MEDIUM("Medium"),
    LARGE("Large")
}

enum class HistoryRetention(override val displayName: String) : ApexSetting {
    SEVEN_DAYS("7 days"),
    THIRTY_DAYS("30 days"),
    NINETY_DAYS("90 days"),
    ONE_YEAR("1 year"),
    FOREVER("Forever")
}

enum class NowPlayingStyle(override val displayName: String) : ApexSetting {
    COMPACT("Compact"),
    EXPANDED("Expanded")
}
