package com.silvercat.sparkcards.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = SparkGoldDark,
    onPrimary = InkNavy,
    secondary = InkNavy,
    background = Cream,
    surface = Cream,
    surfaceVariant = CreamDim,
    onBackground = InkNavy,
    onSurface = InkNavy,
)

private val DarkColors = darkColorScheme(
    primary = SparkGold,
    onPrimary = InkNavy,
    secondary = SparkGold,
    background = InkNavy,
    surface = InkNavy,
    surfaceVariant = InkNavyLight,
    onBackground = Cream,
    onSurface = Cream,
)

@Composable
fun SparkCardsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
