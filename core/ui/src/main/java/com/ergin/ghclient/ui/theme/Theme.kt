package com.ergin.ghclient.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Cathode Terminal HUD Dark Color Scheme (DESIGN.md)
private val CathodeTerminalColorScheme = darkColorScheme(
    primary = CathodePrimary,
    onPrimary = CathodeOnPrimary,
    primaryContainer = CathodePrimaryContainer,
    onPrimaryContainer = CathodeOnPrimaryContainer,
    secondary = CathodeSecondary,
    onSecondary = CathodeOnSecondary,
    secondaryContainer = CathodeSecondaryContainer,
    onSecondaryContainer = CathodeOnSecondaryContainer,
    tertiary = CathodeTertiary,
    onTertiary = CathodeOnTertiary,
    tertiaryContainer = CathodeTertiaryContainer,
    onTertiaryContainer = CathodeOnTertiaryContainer,
    error = CathodeError,
    onError = CathodeOnError,
    errorContainer = CathodeErrorContainer,
    onErrorContainer = CathodeOnErrorContainer,
    background = CathodeBackground,
    onBackground = CathodeOnBackground,
    surface = CathodeSurface,
    onSurface = CathodeOnSurface,
    surfaceVariant = CathodeContainerHighest,
    onSurfaceVariant = CathodeOnSurfaceVariant,
    outline = CathodeOutline,
    outlineVariant = CathodeOutlineVariant
)

// Sharp 0px / Cut Corner Industrial Geometry (DESIGN.md)
val CathodeShapes = Shapes(
    extraSmall = CutCornerShape(0.dp),
    small = CutCornerShape(0.dp),
    medium = CutCornerShape(0.dp),
    large = CutCornerShape(0.dp),
    extraLarge = CutCornerShape(0.dp)
)

@Composable
fun GHClientTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CathodeTerminalColorScheme,
        typography = Typography,
        shapes = CathodeShapes,
        content = content
    )
}
