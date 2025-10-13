package net.vpg.apex.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
    val theme = evaluateFlow<ThemeMode>(PreferenceKeys.THEME)
    val accentColor = evaluateFlow<AccentColor>(PreferenceKeys.ACCENT_COLOR)
    val animationSpeed = dataStore.data.map { it[PreferenceKeys.ANIMATION_SPEED] ?: 1.0f }
    val marqueeSpeed = dataStore.data.map { it[PreferenceKeys.MARQUEE_SPEED] ?: 1.0f }
    val gridSize = evaluateFlow<GridSize>(PreferenceKeys.GRID_SIZE)

    private inline fun <reified T> evaluateFlow(key: Preferences.Key<String>)
            where T : Enum<T>, T : ApexSetting =
        dataStore.data.map { preferences ->
            try {
                enumValueOf(preferences[key]!!)
            } catch (_: Exception) {
                enumValues<T>().first()
            }
        }

    object PreferenceKeys {
        val THEME = stringPreferencesKey("theme")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val ANIMATION_SPEED = floatPreferencesKey("animation_speed")
        val MARQUEE_SPEED = floatPreferencesKey("marquee_speed")
        val GRID_SIZE = stringPreferencesKey("grid_size")
    }

    fun updateTheme(theme: ThemeMode) = updatePreference(PreferenceKeys.THEME, theme.name)

    fun updateAccentColor(color: AccentColor) = updatePreference(PreferenceKeys.ACCENT_COLOR, color.name)

    fun updateAnimationSpeed(speed: Float) = updatePreference(PreferenceKeys.ANIMATION_SPEED, speed)

    fun updateMarqueeSpeed(speed: Float) = updatePreference(PreferenceKeys.MARQUEE_SPEED, speed)

    fun updateGridSize(size: GridSize) = updatePreference(PreferenceKeys.GRID_SIZE, size.name)

    fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        scope.launch {
            dataStore.edit { it[key] = value }
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
    RED("Red", 0xFFD32F2F, 0xFFEF5350, 0xFFB71C1C),
    GREEN("Green", 0xFF1DB954, 0xFF1ED760, 0xFF1AA34A),
    BLUE("Blue", 0xFF1976D2, 0xFF42A5F5, 0xFF0D47A1),
    PURPLE("Purple", 0xFF7B1FA2, 0xFFAB47BC, 0xFF4A148C),
    ORANGE("Orange", 0xFFFF9800, 0xFFFFB74D, 0xFFE65100)
}

enum class GridSize(
    override val displayName: String,
    val cardSize: Int,
    val barSize: Int = cardSize / 2
) : ApexSetting {
    COMPACT("Compact", 120),
    MEDIUM("Medium", 150),
    LARGE("Large", 180)
}
