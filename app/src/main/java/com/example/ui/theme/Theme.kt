package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OracleColorScheme = darkColorScheme(
    primary = AgedGold,
    onPrimary = ObsidianBlack,
    secondary = CharcoalSlate,
    onSecondary = ParchmentWhite,
    background = ObsidianBlack,
    onBackground = ParchmentWhite,
    surface = DeepCharcoal,
    onSurface = ParchmentWhite,
    surfaceVariant = CharcoalSlate,
    onSurfaceVariant = MutedSlate,
    tertiary = ShadowRed,
    onTertiary = ParchmentWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark mode for thematic consistency
    dynamicColor: Boolean = false, // Disable dynamic colors to keep golden gothic vibes
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OracleColorScheme,
        typography = Typography,
        content = content
    )
}
