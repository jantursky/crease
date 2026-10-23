package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldState
import com.jantursky.crease.ProvideFoldState
import com.jantursky.crease.samples.design.CreaseTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class RecipesAppTest {
    private val wide = Size(1000f, 700f)

    private fun androidx.compose.ui.test.ComposeUiTest.show(
        state: FoldState,
        modifier: Modifier = Modifier,
    ) = setContent {
        ProvideFoldState(state) { CreaseTheme { Box(modifier) { RecipesApp() } } }
    }

    @Test
    fun bookShowsIngredientsAndStepsSideBySide() =
        runComposeUiTest {
            show(FoldState.book(wide, angle = 100f, foldWidth = 40f))
            onNodeWithText("Ingredients").assertIsDisplayed()
            onNodeWithText("Steps").assertIsDisplayed()
            onNodeWithText("Now cooking").assertIsDisplayed()
            onNodeWithText("Previous").assertDoesNotExist()
        }

    @Test
    fun tabletopShowsTimerAboveAndControlsBelow() =
        runComposeUiTest {
            show(FoldState.tabletop(Size(700f, 1000f), angle = 100f))
            onNodeWithText("Technique video").assertIsDisplayed()
            onNodeWithText("Previous").assertIsDisplayed()
            onNodeWithText("Next").assertIsDisplayed()
            onNodeWithText("Ingredients").assertDoesNotExist()
        }

    @Test
    fun closedShowsOneCompactStepCard() =
        runComposeUiTest {
            show(FoldState.closed())
            onNodeWithText("Step 1 of 6").assertIsDisplayed()
            onNodeWithText("Next").assertIsDisplayed()
            onNodeWithText("Ingredients").assertDoesNotExist()
            onNodeWithText("Steps").assertDoesNotExist()
        }

    @Test
    fun wideFlatShowsBothPanes() =
        runComposeUiTest {
            show(FoldState.flat(wide))
            onNodeWithText("Ingredients").assertIsDisplayed()
            onNodeWithText("Steps").assertIsDisplayed()
        }

    @Test
    fun narrowFlatShowsTabs() =
        runComposeUiTest {
            show(FoldState.flat(Size(360f, 700f)), Modifier.size(360.dp, 700.dp))
            onNode(hasText("Ingredients") and isSelectable()).assertIsDisplayed()
            onNode(hasText("Steps") and isSelectable()).assertIsDisplayed()
            onNode(hasText("Ingredients") and isSelectable()).performClick()
            onNodeWithText("Tomato spaghetti").assertIsDisplayed()
        }

    @Test
    fun nextAdvancesTheStep() =
        runComposeUiTest {
            show(FoldState.closed())
            onNodeWithText("Next").performClick()
            onNodeWithText("Step 2 of 6").assertIsDisplayed()
            onNodeWithText("Start the sauce").assertIsDisplayed()
        }
}
