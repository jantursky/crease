package com.jantursky.crease.samples.recipes

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap

/** What the cook is doing right now: which recipe and step, and the step timer. */
@Stable
class CookingSession(
    val recipes: List<Recipe>,
) {
    var recipeIndex by mutableIntStateOf(0)
        private set
    var stepIndex by mutableIntStateOf(0)
        private set
    var remainingSeconds by mutableIntStateOf(recipes[0].steps[0].totalSeconds())
        private set
    var isRunning by mutableStateOf(false)
        private set

    private val checked: SnapshotStateMap<Pair<Int, Int>, Boolean> = mutableStateMapOf()

    val recipe: Recipe get() = recipes[recipeIndex]
    val step: Step get() = recipe.steps[stepIndex]
    val hasTimer: Boolean get() = step.minutes != null
    val hasNext: Boolean get() = stepIndex < recipe.steps.lastIndex
    val hasPrevious: Boolean get() = stepIndex > 0

    /** Elapsed share of the current step's timer, 0 to 1. */
    val progress: Float
        get() {
            val total = step.totalSeconds()
            return if (total == 0) 0f else 1f - remainingSeconds / total.toFloat()
        }

    fun selectRecipe(index: Int) {
        recipeIndex = index
        selectStep(0)
    }

    fun selectStep(index: Int) {
        stepIndex = index
        remainingSeconds = step.totalSeconds()
        isRunning = false
    }

    fun next() {
        if (hasNext) selectStep(stepIndex + 1)
    }

    fun previous() {
        if (hasPrevious) selectStep(stepIndex - 1)
    }

    fun toggleTimer() {
        if (!hasTimer) return
        if (remainingSeconds == 0) remainingSeconds = step.totalSeconds()
        isRunning = !isRunning
    }

    /** Advances the running timer by one second. */
    fun tick() {
        if (!isRunning) return
        remainingSeconds -= 1
        if (remainingSeconds <= 0) {
            remainingSeconds = 0
            isRunning = false
        }
    }

    fun isChecked(ingredient: Int): Boolean = checked[recipeIndex to ingredient] == true

    fun toggleChecked(ingredient: Int) {
        val key = recipeIndex to ingredient
        checked[key] = !isChecked(ingredient)
    }
}

private fun Step.totalSeconds(): Int = (minutes ?: 0) * 60
