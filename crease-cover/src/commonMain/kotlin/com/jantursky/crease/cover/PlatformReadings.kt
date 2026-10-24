package com.jantursky.crease.cover

/**
 * Maps what a platform reports to a [CoverDisplayState]. Each platform reduces its own API to these three
 * facts, so the rule that turns them into a state is the same everywhere.
 *
 * - [supported]: the system has a cover display feature at all.
 * - [available]: the system would show the content now.
 * - [presenting]: the content is on the outer display.
 */
internal fun coverDisplayState(
    supported: Boolean,
    available: Boolean,
    presenting: Boolean,
): CoverDisplayState =
    when {
        !supported -> CoverDisplayState.Unsupported
        presenting -> CoverDisplayState.Active
        available -> CoverDisplayState.Available
        else -> CoverDisplayState.Unavailable
    }
