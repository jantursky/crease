package com.jantursky.crease.samples.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.jantursky.crease.samples.design.resources.Res
import com.jantursky.crease.samples.design.resources.inter_regular
import com.jantursky.crease.samples.design.resources.inter_semibold
import org.jetbrains.compose.resources.Font

/** Type scale from design/DESIGN.md. Weight 700 is never used. */
@Immutable
class CreaseTypography(
    val heading: TextStyle,
    val title: TextStyle,
    val kicker: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
    /** Tabular figures for angles, timers and quantities. */
    val numeric: TextStyle,
)

@Composable
internal fun rememberInterFamily(): FontFamily =
    FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_semibold, FontWeight.SemiBold),
    )

internal fun creaseTypography(family: FontFamily): CreaseTypography {
    fun style(
        size: Int,
        line: Int,
        weight: FontWeight,
        tracking: Float,
    ) = TextStyle(
        fontFamily = family,
        fontSize = size.sp,
        lineHeight = line.sp,
        fontWeight = weight,
        letterSpacing = tracking.sp,
    )

    val regular = FontWeight.Normal
    val semibold = FontWeight.SemiBold
    val body = style(17, 25, regular, -0.37f)
    return CreaseTypography(
        heading = style(40, 44, semibold, 0f),
        title = style(24, 28, semibold, 0f),
        kicker = style(21, 24, semibold, 0.23f),
        body = body,
        bodySmall = style(14, 18, regular, -0.22f),
        caption = style(12, 16, regular, -0.12f),
        label = style(12, 16, semibold, -0.12f),
        numeric = body.copy(fontFeatureSettings = "tnum", letterSpacing = TextUnit.Unspecified),
    )
}
