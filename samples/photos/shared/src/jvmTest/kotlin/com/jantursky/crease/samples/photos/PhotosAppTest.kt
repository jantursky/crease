package com.jantursky.crease.samples.photos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldState
import com.jantursky.crease.ProvideFoldState
import com.jantursky.crease.samples.design.CreaseTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PhotosAppTest {
    private fun ComposeUiTest.show(
        state: FoldState,
        modifier: Modifier = Modifier,
    ) = setContent {
        ProvideFoldState(state) { CreaseTheme { Box(modifier) { PhotosApp() } } }
    }

    @Test
    fun bookShowsLibraryAndEditor() =
        runComposeUiTest {
            show(FoldState.book(Size(1000f, 700f), angle = 110f, foldWidth = 40f))
            onNodeWithText("Library").assertIsDisplayed()
            onNodeWithText("Adjust").assertIsDisplayed()
            onNodeWithText("Exposure").assertIsDisplayed()
        }

    @Test
    fun tabletopShowsSlidersBelowThePhoto() =
        runComposeUiTest {
            show(FoldState.tabletop(Size(700f, 1000f), angle = 100f))
            onNodeWithText("Adjust").assertIsDisplayed()
            onNodeWithText("Warmth").assertIsDisplayed()
            onNodeWithText("Library").assertDoesNotExist()
        }

    @Test
    fun closedIsASimpleViewer() =
        runComposeUiTest {
            show(FoldState.closed())
            onNodeWithText("Harbour dusk").assertIsDisplayed()
            onNodeWithText("Exposure").assertDoesNotExist()
            onNodeWithText("Next").performClick()
            onNodeWithText("Alpine lake").assertIsDisplayed()
        }

    @Test
    fun narrowFlatShowsFilmstripAndSliders() =
        runComposeUiTest {
            show(FoldState.flat(Size(360f, 760f)), Modifier.size(360.dp, 760.dp))
            onNodeWithText("Exposure").assertIsDisplayed()
            onNodeWithText("Library").assertDoesNotExist()
        }
}
