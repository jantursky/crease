package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseCard
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.StatusLabel
import com.jantursky.crease.samples.design.TogglePill

@Composable
fun IngredientsPane(
    session: CookingSession,
    modifier: Modifier = Modifier,
) {
    val colors = CreaseTheme.colors
    val type = CreaseTheme.type
    CreaseCard(modifier = modifier.fillMaxSize(), padding = 24.dp) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                session.recipes.forEachIndexed { index, recipe ->
                    TogglePill(recipe.shortTitle, index == session.recipeIndex, { session.selectRecipe(index) })
                }
            }
            if (session.recipe.isNew) StatusLabel("New", Modifier.padding(top = 24.dp))
            CreaseText(
                session.recipe.title,
                style = type.title,
                modifier = Modifier.padding(top = if (session.recipe.isNew) 4.dp else 24.dp),
            )
            CreaseText(session.recipe.summary, style = type.bodySmall, color = colors.slate)
            CreaseText("Ingredients", style = type.kicker, modifier = Modifier.padding(top = 28.dp, bottom = 8.dp))
            session.recipe.ingredients.forEachIndexed { index, ingredient ->
                val done = session.isChecked(index)
                val tone = if (done) colors.slate else colors.ink
                val decoration = if (done) TextDecoration.LineThrough else TextDecoration.None
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { session.toggleChecked(index) }
                        .padding(vertical = 10.dp),
                ) {
                    CreaseText(
                        ingredient.quantity,
                        style = type.numeric.copy(textDecoration = decoration),
                        color = colors.slate,
                        modifier = Modifier.width(84.dp),
                    )
                    CreaseText(ingredient.name, style = type.body.copy(textDecoration = decoration), color = tone)
                }
                Divider()
            }
        }
    }
}

@Composable
fun StepsPane(
    session: CookingSession,
    modifier: Modifier = Modifier,
    bottomClearance: Dp = 0.dp,
) {
    val colors = CreaseTheme.colors
    val type = CreaseTheme.type
    CreaseCard(modifier = modifier.fillMaxSize(), padding = 24.dp) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            CreaseText("Steps", style = type.kicker, modifier = Modifier.padding(bottom = 12.dp))
            session.recipe.steps.forEachIndexed { index, step ->
                val current = index == session.stepIndex
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(CreaseShapes.floating)
                        .then(if (current) Modifier.background(colors.band) else Modifier)
                        .clickable { session.selectStep(index) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CreaseText(
                        "${index + 1}",
                        style = type.numeric.copy(fontWeight = type.label.fontWeight),
                        color = if (current) colors.signal else colors.slate,
                        modifier = Modifier.width(20.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CreaseText(step.title, style = type.body.copy(fontWeight = type.label.fontWeight))
                        CreaseText(step.text, style = type.bodySmall, color = colors.slate)
                        step.minutes?.let { CreaseText("$it min", style = type.caption, color = colors.slate) }
                    }
                }
            }
            Spacer(Modifier.height(bottomClearance))
        }
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(CreaseTheme.colors.hairline))
}
