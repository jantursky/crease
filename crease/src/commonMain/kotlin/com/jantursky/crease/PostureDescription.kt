package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

/**
 * Describes the current [Posture] to assistive technology, which announces the new text when the posture
 * changes. The text comes from [describe], so the library holds no strings and the app can translate them.
 * A change of the hinge angle alone leaves the text, and so the announcement, as it was.
 *
 * Apply it to a node that is always on screen, such as the root of the screen, or to the layout that
 * changes with the posture.
 *
 * @param describe the text for a posture, for example "Book, two columns".
 */
@Composable
public fun Modifier.postureDescription(describe: (Posture) -> String): Modifier {
    val description = describe(rememberFoldState().posture)
    return semantics {
        stateDescription = description
        liveRegion = LiveRegionMode.Polite
    }
}
