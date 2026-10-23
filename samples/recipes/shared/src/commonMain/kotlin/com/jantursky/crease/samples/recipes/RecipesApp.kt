package com.jantursky.crease.samples.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.Posture
import com.jantursky.crease.TabletopLayout
import com.jantursky.crease.TwoPaneFallback
import com.jantursky.crease.avoidFold
import com.jantursky.crease.rememberFoldState
import com.jantursky.crease.rememberHinge
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.SegmentedControl
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private val CompactWidth = 600.dp

/** The recipe reader. Layout follows the posture; the shading follows the hinge angle. */
@Composable
fun RecipesApp(modifier: Modifier = Modifier) {
    val session = remember { CookingSession(sampleRecipes) }
    LaunchedEffect(session.isRunning) {
        while (session.isRunning) {
            delay(1000.milliseconds)
            session.tick()
        }
    }
    val posture = rememberFoldState().posture
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(CreaseTheme.colors.band)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(8.dp),
    ) {
        when (posture) {
            Posture.Closed -> {
                StepCard(session, Modifier.padding(8.dp))
            }

            Posture.Flat if maxWidth < CompactWidth -> {
                CompactRecipe(session)
            }

            else -> {
                TabletopLayout(
                    top = { StepFocus(session, Modifier.padding(8.dp)) },
                    bottom = { StepCard(session, Modifier.padding(8.dp), standalone = false) },
                    flat = { ReadingLayout(session) },
                )
            }
        }
    }
}

@Composable
private fun ReadingLayout(session: CookingSession) {
    val angle = rememberHinge()?.angle
    val shade = CreaseTheme.colors.ink
    Box(Modifier.fillMaxSize()) {
        FoldAwareTwoPane(
            first = { IngredientsPane(session, Modifier.padding(8.dp)) },
            second = {
                StepsPane(session, Modifier.padding(8.dp).pageShade(angle, shade), bottomClearance = 72.dp)
            },
            fallback = TwoPaneFallback.SideBySide,
        )
        NowCookingBar(
            session,
            Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .avoidFold()
                .padding(16.dp),
        )
    }
}

private enum class CompactTab(
    val label: String,
) {
    Ingredients("Ingredients"),
    Steps("Steps"),
}

@Composable
private fun CompactRecipe(session: CookingSession) {
    var tab by remember { mutableStateOf(CompactTab.Steps) }
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        SegmentedControl(CompactTab.entries, tab, { tab = it }, Modifier.fillMaxWidth(), label = { it.label })
        Box(Modifier.weight(1f).padding(top = 12.dp)) {
            when (tab) {
                CompactTab.Ingredients -> IngredientsPane(session)
                CompactTab.Steps -> StepsPane(session)
            }
        }
    }
}
