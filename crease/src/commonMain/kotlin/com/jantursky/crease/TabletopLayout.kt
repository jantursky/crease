package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

/**
 * Shows [top] above the fold and [bottom] below it while the device stands in [Posture.Tabletop], and
 * [flat] in every other posture.
 */
@Composable
public fun TabletopLayout(
    top: @Composable () -> Unit,
    bottom: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    flat: @Composable () -> Unit = top,
) {
    val state = rememberFoldState()
    val fold = state.fold.takeIf { state.posture == Posture.Tabletop }
    val origin = rememberWindowOrigin()
    // When flat is top itself, it shares top's slot, so its state survives a change of posture.
    val flatSlot = if (flat === top) TabletopSlot.Top else TabletopSlot.Flat
    SubcomposeLayout(modifier.trackWindowOrigin(origin)) { constraints ->
        val area = constraints.fillArea()
        val split = splitAtWindowFold(area, fold, origin.value)
        val placed =
            if (split != null) {
                listOf(
                    measurePane(TabletopSlot.Top, split.first, top),
                    measurePane(TabletopSlot.Bottom, split.second, bottom),
                )
            } else {
                listOf(measurePane(flatSlot, area, flat))
            }
        layoutPanes(constraints, placed)
    }
}

private enum class TabletopSlot { Top, Bottom, Flat }
