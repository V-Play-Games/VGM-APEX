package net.vpg.apex.ui.screens

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.vpg.apex.core.*
import net.vpg.apex.core.di.rememberSettings

object SettingsScreen : ApexScreenStatic(
    route = "settings",
    columnModifier = Modifier.fillMaxSize(),
    content = {
        SettingsContent()
    }
)

@Composable
fun SettingsContent() {
    val settings = rememberSettings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ChoiceSettingItem(
            title = "Theme",
            subtitle = "Choose your app appearance",
            icon = Icons.Default.Brightness6,
            currentChoice = settings.theme.asStateValue(),
            choices = ThemeMode.entries,
            onChoiceSelected = { settings.updateTheme(it) }
        )

        ChoiceSettingItem(
            title = "Accent Color",
            subtitle = "Customize app color scheme",
            icon = Icons.Default.Palette,
            currentChoice = settings.accentColor.asStateValue(),
            choices = AccentColor.entries,
            onChoiceSelected = { settings.updateAccentColor(it) }
        )

        SpeedSettingItem(
            title = "Animation Speed",
            subtitle = "Control shimmer and transition speeds",
            icon = Icons.Default.Speed,
            currentSpeed = settings.animationSpeed.collectAsState(1.0f).value,
            valueRange = 0.5f..2.0f,
            steps = 2,
            onSpeedChange = { settings.updateAnimationSpeed(it) }
        )

        SpeedSettingItem(
            title = "Marquee Speed",
            subtitle = "Text scrolling speed for long titles",
            icon = Icons.Default.TextFields,
            currentSpeed = settings.marqueeSpeed.collectAsState(1.0f).value,
            valueRange = 0.5f..3.0f,
            steps = 4,
            onSpeedChange = { settings.updateMarqueeSpeed(it) }
        )

        ChoiceSettingItem(
            title = "Grid Size",
            subtitle = "Album and track grid density",
            icon = Icons.Default.GridView,
            currentChoice = settings.gridSize.asStateValue(),
            choices = GridSize.entries,
            onChoiceSelected = { settings.updateGridSize(it) }
        )

        ChoiceSettingItem(
            title = "History Retention",
            subtitle = "How long to keep search and play history",
            icon = Icons.Default.History,
            currentChoice = settings.historyRetention.asStateValue(),
            choices = HistoryRetention.entries,
            onChoiceSelected = { settings.updateHistoryRetention(it) }
        )
    }
}

// Helper Composable Functions
@Composable
private fun <T> ChoiceSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    currentChoice: T,
    choices: List<T>,
    onChoiceSelected: (T) -> Unit
) where T : ApexSetting, T : Enum<T> {
    SettingItem(
        title = title,
        subtitle = subtitle,
        icon = icon
    ) {
        Column(modifier = Modifier.selectableGroup()) {
            choices.forEach { choice ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = currentChoice == choice,
                            onClick = { onChoiceSelected(choice) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentChoice == choice,
                        onClick = null
                    )
                    Text(
                        text = choice.displayName,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    currentSpeed: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    @IntRange(from = 0) steps: Int,
    onSpeedChange: (Float) -> Unit
) {
    SettingItem(
        title = title,
        subtitle = subtitle,
        icon = icon
    ) {
        Column {
            Slider(
                value = currentSpeed,
                onValueChange = onSpeedChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Slow", style = MaterialTheme.typography.bodySmall)
                Text("${"%.1f".format(currentSpeed)}x", style = MaterialTheme.typography.bodySmall)
                Text("Fast", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .bounceClick(onClick = { expanded = !expanded })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        if (expanded) {
            content()
        }
    }
}
