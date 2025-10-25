package com.kashmir.thefilamentary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mint600,
    onPrimary = Color.White,
    primaryContainer = Mint700,
    onPrimaryContainer = Color.White,
    secondary = Mint700,
    onSecondary = Color.White,
    secondaryContainer = Mint900,
    onSecondaryContainer = Color.White,
    tertiary = Mint700,
    background = Color(0xFF101314),
    surface = Color(0xFF121517),
    onBackground = Color(0xFFE6E9EB),
    onSurface = Color(0xFFE6E9EB),
    outline = NeutralOutline
)

private val LightColorScheme = lightColorScheme(
    primary = Mint600,
    onPrimary = Color.White,
    primaryContainer = Mint100,
    onPrimaryContainer = Mint900,
    secondary = Mint700,
    onSecondary = Color.White,
    secondaryContainer = Mint100,
    onSecondaryContainer = Mint900,
    tertiary = Mint700,
    background = Color.White,
    surface = NeutralSurface,
    onBackground = Color(0xFF111314),
    onSurface = Color(0xFF111314),
    outline = NeutralOutline
)

@Composable
fun TheFilamentaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Fix palette to mint aesthetic; disable dynamic to match provided screenshot
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}