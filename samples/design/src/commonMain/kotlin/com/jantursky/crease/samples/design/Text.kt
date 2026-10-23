package com.jantursky.crease.samples.design

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CreaseText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = CreaseTheme.type.body,
    color: Color = CreaseTheme.colors.ink,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color, textAlign = textAlign),
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Bare signal-colored text, never boxed. */
@Composable
fun StatusLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    CreaseText(text, modifier, style = CreaseTheme.type.label, color = CreaseTheme.colors.signal)
}
