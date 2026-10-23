package com.jantursky.crease.samples.photos

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue

/** Which photo is open and the adjustments made to each. */
@Stable
class EditorSession(
    val photos: List<Photo>,
) {
    var selectedIndex by mutableIntStateOf(0)
        private set

    private val edits = mutableStateMapOf<Int, Adjustments>()

    val photo: Photo get() = photos[selectedIndex]
    val adjustments: Adjustments get() = adjustmentsOf(selectedIndex)

    fun adjustmentsOf(index: Int): Adjustments = edits[index] ?: Adjustments()

    fun select(index: Int) {
        selectedIndex = index.coerceIn(photos.indices)
    }

    fun update(change: (Adjustments) -> Adjustments) {
        edits[selectedIndex] = change(adjustments)
    }

    fun reset() {
        edits.remove(selectedIndex)
    }
}
