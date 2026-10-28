package com.jantursky.crease

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * The current [FoldState] as a [StateFlow], for a view-model or any code outside composition.
 *
 * The flow follows [rememberFoldState], including a state set by [ProvideFoldState], and starts with the
 * current state. The platform reports folds per window, so the flow only updates while this call is in the
 * composition; after it leaves, collectors keep the last value. Call it high in the tree and pass the flow
 * down.
 */
@Composable
public fun rememberFoldStateFlow(): StateFlow<FoldState> {
    val state = rememberFoldState()
    val flow = remember { MutableStateFlow(state) }
    SideEffect { flow.value = state }
    return flow
}

/** The [Posture] of each [FoldState], starting with the current one and then only when it changes. */
public fun StateFlow<FoldState>.postures(): Flow<Posture> = map { it.posture }.distinctUntilChanged()

/**
 * The latest change of [Posture].
 *
 * @property previous the posture before the latest change, equal to [current] until the posture first changes.
 * @property current the current posture.
 */
@Immutable
public class PostureTransition(
    public val previous: Posture,
    public val current: Posture,
) {
    /** Whether the posture has changed since the composition began. */
    public val changed: Boolean get() = previous != current

    override fun equals(other: Any?): Boolean =
        other is PostureTransition && previous == other.previous && current == other.current

    override fun hashCode(): Int = 31 * previous.hashCode() + current.hashCode()

    override fun toString(): String = "PostureTransition(previous=$previous, current=$current)"
}

/**
 * The latest change of posture, to animate between layouts. Recomposes when the posture changes, not when
 * only the hinge angle does.
 */
@Composable
public fun rememberPostureTransition(): PostureTransition {
    val current = rememberFoldState().posture
    val last = remember { PostureHolder(current) }
    val transition = remember(current) { PostureTransition(last.value, current) }
    SideEffect { last.value = current }
    return transition
}

private class PostureHolder(
    var value: Posture,
)
