package com.jantursky.crease.samples.design

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Color tokens from design/DESIGN.md. */
@Immutable
class CreaseColors(
    val canvas: Color,
    val band: Color,
    val raised: Color,
    val card: Color,
    val hairline: Color,
    val control: Color,
    val ink: Color,
    val slate: Color,
    val steel: Color,
    val link: Color,
    val action: Color,
    val signal: Color,
    val fold: Color,
    val isDark: Boolean,
    val onAction: Color = Color.White,
)

internal val LightColors =
    CreaseColors(
        canvas = Color(0xFFFFFFFF),
        band = Color(0xFFF5F5F7),
        raised = Color(0xFFFAFAFC),
        card = Color(0xFFFFFFFF),
        hairline = Color(0xFFD6D6D6),
        control = Color(0xFFE6E6E8),
        ink = Color(0xFF1D1D1F),
        slate = Color(0xFF707070),
        steel = Color(0xFF86868B),
        link = Color(0xFF0066CC),
        action = Color(0xFF0071E3),
        signal = Color(0xFFB64400),
        fold = Color(0x1F0071E3),
        isDark = false,
    )

internal val DarkColors =
    CreaseColors(
        canvas = Color(0xFF000000),
        band = Color(0xFF161617),
        raised = Color(0xFF1D1D1F),
        card = Color(0xFF1D1D1F),
        hairline = Color(0xFF424245),
        control = Color(0xFF2C2C2E),
        ink = Color(0xFFF5F5F7),
        slate = Color(0xFFA1A1A6),
        steel = Color(0xFF86868B),
        link = Color(0xFF2997FF),
        action = Color(0xFF0071E3),
        signal = Color(0xFFFF791B),
        fold = Color(0x2E2997FF),
        isDark = true,
    )
