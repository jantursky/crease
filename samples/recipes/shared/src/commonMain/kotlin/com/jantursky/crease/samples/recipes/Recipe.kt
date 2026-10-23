package com.jantursky.crease.samples.recipes

class Ingredient(
    val quantity: String,
    val name: String,
)

/** One step of a recipe. [minutes] is `null` when nothing needs timing. */
class Step(
    val title: String,
    val text: String,
    val minutes: Int? = null,
)

class Recipe(
    val title: String,
    val shortTitle: String,
    val summary: String,
    val isNew: Boolean,
    val ingredients: List<Ingredient>,
    val steps: List<Step>,
)

/** `m:ss` for a countdown, with minutes padded to two digits. */
fun formatClock(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}
