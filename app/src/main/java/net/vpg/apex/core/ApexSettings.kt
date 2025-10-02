package net.vpg.apex.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "apex_settings")

class ApexSettings(context: Context) {
    private val dataStore = context.dataStore
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val theme = dataStore.data.map { preferences ->
        try {
            ThemeMode.valueOf(preferences[PreferenceKeys.THEME]!!)
        } catch (_: Exception) {
            ThemeMode.AUTO
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

enum class ThemeMode(val displayName: String) {
    AUTO("Auto"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AccentColor(val displayName: String) {
    GREEN("Green"),
    BLUE("Blue"),
    PURPLE("Purple"),
    ORANGE("Orange"),
    RED("Red")
}

enum class GridSize(val displayName: String) {
    COMPACT("Compact"),
    MEDIUM("Medium"),
    LARGE("Large")
}

enum class HistoryRetention(val displayName: String) {
    SEVEN_DAYS("7 days"),
    THIRTY_DAYS("30 days"),
    NINETY_DAYS("90 days"),
    ONE_YEAR("1 year"),
    FOREVER("Forever")
}

enum class NowPlayingStyle(val displayName: String) {
    COMPACT("Compact"),
    EXPANDED("Expanded")
}
