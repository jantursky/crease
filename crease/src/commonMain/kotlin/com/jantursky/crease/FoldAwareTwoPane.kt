package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout

/** What [FoldAwareTwoPane] shows when no separating fold crosses it. */
public enum class TwoPaneFallback {
    /** Only the first pane. */
    FirstOnly,

    /** Both panes in a row, half each. */
    SideBySide,

    /** Both panes in a column, half each. */
    Stacked,
}

/**
 * Two panes split at the fold: side by side across a vertical fold, one above the other across a
 * horizontal one. Neither pane is drawn under an occluding fold. Without a separating fold the layout
 * follows [fallback].
 */
@Composable
public fun FoldAwareTwoPane(
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    fallback: TwoPaneFallback = TwoPaneFallback.FirstOnly,
) {
    val fold = rememberFoldState().separatingFold
    val origin = rememberWindowOrigin()
    SubcomposeLayout(modifier.trackWindowOrigin(origin)) { constraints ->
        val area = constraints.fillArea()
        val split = splitAtWindowFold(area, fold, origin.value)
        val panes =
            when {
                split != null -> {
                    listOf(
                        measurePane(TwoPaneSlot.First, split.first, first),
                        measurePane(TwoPaneSlot.Second, split.second, second),
                    )
                }

                fallback == TwoPaneFallback.FirstOnly -> {
                    listOf(measurePane(TwoPaneSlot.First, area, first))
                }

                fallback == TwoPaneFallback.SideBySide && constraints.hasBoundedWidth -> {
                    val middle = area.width / 2f
                    listOf(
                        measurePane(TwoPaneSlot.First, area.copy(right = middle), first),
                        measurePane(TwoPaneSlot.Second, area.copy(left = middle), second),
                    )
                }

                fallback == TwoPaneFallback.Stacked && constraints.hasBoundedHeight -> {
                    val middle = area.height / 2f
                    listOf(
                        measurePane(TwoPaneSlot.First, area.copy(bottom = middle), first),
                        measurePane(TwoPaneSlot.Second, area.copy(top = middle), second),
                    )
                }

                // With no end to split in half, each pane takes the length its content needs.
                fallback == TwoPaneFallback.SideBySide -> {
                    val firstPane = measurePane(TwoPaneSlot.First, area, first)
                    listOf(
                        firstPane,
                        measurePane(TwoPaneSlot.Second, area.copy(left = firstPane.right.toFloat()), second),
                    )
                }

                else -> {
                    val firstPane = measurePane(TwoPaneSlot.First, area, first)
                    listOf(
                        firstPane,
                        measurePane(TwoPaneSlot.Second, area.copy(top = firstPane.bottom.toFloat()), second),
                    )
                }
            }
        layoutPanes(constraints, panes)
    }
}

private enum class TwoPaneSlot { First, Second }
