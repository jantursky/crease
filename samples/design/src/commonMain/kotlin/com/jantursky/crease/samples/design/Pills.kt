package com.jantursky.crease.samples.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
private fun Pill(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    fill: Color?,
    ring: Color?,
    textColor: Color,
    modifier: Modifier,
) {
    val shape = CreaseShapes.pill
    CreaseText(
        text = text,
        style = CreaseTheme.type.label,
        color = textColor,
        maxLines = 1,
        modifier =
            modifier
                .clip(shape)
                .then(if (fill != null) Modifier.background(fill) else Modifier)
                .then(if (ring != null) Modifier.border(1.dp, ring, shape) else Modifier)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

/** The primary call to action; the only place `action` is used as a fill. */
@Composable
fun FilledPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = CreaseTheme.colors
    Pill(
        text,
        onClick,
        enabled,
        fill = if (enabled) colors.action else colors.control,
        ring = null,
        textColor = if (enabled) colors.onAction else colors.slate,
        modifier = modifier,
    )
}

@Composable
fun OutlinedPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = CreaseTheme.colors
    Pill(
        text,
        onClick,
        enabled,
        fill = null,
        ring = if (enabled) colors.steel else colors.control,
        textColor = if (enabled) colors.ink else colors.slate,
        modifier = modifier,
    )
}

/** A two-state pill: outlined when off, solid ink when on. */
@Composable
fun TogglePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CreaseTheme.colors
    Pill(
        text,
        onClick,
        enabled = true,
        fill = if (selected) colors.ink else null,
        ring = if (selected) null else colors.steel,
        textColor = if (selected) colors.canvas else colors.ink,
        modifier = modifier,
    )
}
