package com.jantursky.crease.samples.recipes

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.jantursky.crease.samples.design.CreaseShapes

private const val MAX_SHADE = 0.14f

/** How strongly the lifted page is shaded: nothing when flat (180°), most when nearly shut. */
internal fun shadeAlpha(angle: Float?): Float =
    if (angle ==
        null
    ) {
        0f
    } else {
        (180f - angle).coerceIn(0f, 180f) / 180f * MAX_SHADE
    }

/** Tints the page as the hinge closes, like a page turning away from the light. */
internal fun Modifier.pageShade(
    angle: Float?,
    color: Color,
): Modifier =
    clip(CreaseShapes.card).drawWithContent {
        drawContent()
        drawRect(color.copy(alpha = shadeAlpha(angle)))
    }
