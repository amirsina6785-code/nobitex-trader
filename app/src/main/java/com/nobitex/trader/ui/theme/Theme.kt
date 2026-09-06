package com.nobitex.trader.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF00D4FF),
    secondary = Color(0xFF8B5CF6),
    tertiary = Color(0xFF06D6A0),
    background = Color(0xFF0B0D12),
    surface = Color(0xFF141821),
    error = Color(0xFFEF476F)
)

@Composable
fun NobitexTraderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
