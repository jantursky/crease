package com.jantursky.crease.samples.web.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import com.jantursky.crease.rememberFoldAvoidingPopupPositionProvider
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.FilledPill

/** Opens right under the button. The fold-aware provider moves it off the crease when it would cross. */
private val BelowButton =
    object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowSize: IntSize,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
        ): IntOffset = IntOffset(anchorBounds.left, anchorBounds.bottom)
    }

/** A menu that opens beside its button and never sits across the fold. */
@Composable
fun FoldAvoidingMenuExample() {
    var open by remember { mutableStateOf(false) }
    val provider = rememberFoldAvoidingPopupPositionProvider(BelowButton)
    Box(Modifier.fillMaxSize().background(CreaseTheme.colors.band)) {
        Box(Modifier.align(Alignment.Center).offset(x = (-24).dp)) {
            FilledPill("Open menu", onClick = { open = !open })
            if (open) {
                Popup(popupPositionProvider = provider, onDismissRequest = { open = false }) {
                    Column(
                        Modifier
                            .padding(top = 8.dp)
                            .width(200.dp)
                            .clip(CreaseShapes.floating)
                            .background(CreaseTheme.colors.card)
                            .padding(12.dp),
                    ) {
                        CreaseText("Copy", style = CreaseTheme.type.bodySmall)
                        CreaseText("Paste", style = CreaseTheme.type.bodySmall)
                        CreaseText("Delete", style = CreaseTheme.type.bodySmall, color = CreaseTheme.colors.slate)
                    }
                }
            }
        }
    }
}
