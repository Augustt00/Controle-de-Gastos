package com.example.controlegastos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ControleGastosColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppSurface,
    primaryContainer = AppPrimaryContainer,
    onPrimaryContainer = AppText,
    secondary = AppText,
    onSecondary = AppSurface,
    secondaryContainer = AppSurfaceVariant,
    onSecondaryContainer = AppText,
    tertiary = AppPrimary,
    onTertiary = AppSurface,
    background = AppBackground,
    onBackground = AppText,
    surface = AppSurface,
    onSurface = AppText,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppText,
    outline = AppOutline,
    error = AppError
)

@Composable
fun ControleGastosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ControleGastosColorScheme,
        typography = Typography,
        content = content
    )
}