package com.jantursky.crease.samples.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/** Corner radii from design/DESIGN.md. */
object CreaseShapes {
    val card = RoundedCornerShape(28.dp)
    val floating = RoundedCornerShape(20.dp)
    val link = RoundedCornerShape(10.dp)
    val pill = CircleShape
}

private val LocalColors = staticCompositionLocalOf { LightColors }
private val LocalTypography = staticCompositionLocalOf<CreaseTypography> { error("CreaseTheme missing") }

object CreaseTheme {
    val colors: CreaseColors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current
    val type: CreaseTypography
        @Composable @ReadOnlyComposable
        get() = LocalTypography.current
    val shapes: CreaseShapes get() = CreaseShapes
}

@Composable
fun CreaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val family = rememberInterFamily()
    val typography = remember(family) { creaseTypography(family) }
    CompositionLocalProvider(
        LocalColors provides if (darkTheme) DarkColors else LightColors,
        LocalTypography provides typography,
        content = content,
    )
}
