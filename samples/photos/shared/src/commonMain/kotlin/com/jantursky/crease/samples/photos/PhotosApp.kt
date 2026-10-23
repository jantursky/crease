package com.jantursky.crease.samples.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.Posture
import com.jantursky.crease.TabletopLayout
import com.jantursky.crease.TwoPaneFallback
import com.jantursky.crease.rememberFoldState
import com.jantursky.crease.samples.design.CreaseCard
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme
import com.jantursky.crease.samples.design.OutlinedPill

private val CompactWidth = 600.dp

/** The photo editor. Layout follows the posture; the preview leans with the hinge angle. */
@Composable
fun PhotosApp(modifier: Modifier = Modifier) {
    val session = remember { EditorSession(samplePhotos) }
    val foldState = rememberFoldState()
    val tilt = tiltDegrees(foldState.hinge?.angle)
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(CreaseTheme.colors.band)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(8.dp),
    ) {
        when {
            foldState.posture == Posture.Closed -> {
                Viewer(session)
            }

            foldState.posture == Posture.Flat && maxWidth < CompactWidth -> {
                CompactEditor(session)
            }

            else -> {
                TabletopLayout(
                    top = { PhotoPreview(session, tilt, tiltAroundX = true, Modifier.fillMaxSize().padding(20.dp)) },
                    bottom = { AdjustCard(session, Modifier.padding(8.dp)) },
                    flat = { Workspace(session, tilt) },
                )
            }
        }
    }
}

@Composable
private fun AdjustCard(
    session: EditorSession,
    modifier: Modifier = Modifier,
) {
    CreaseCard(modifier.fillMaxSize(), padding = 24.dp) { AdjustmentPanel(session) }
}

@Composable
private fun Workspace(
    session: EditorSession,
    tilt: Float,
) {
    FoldAwareTwoPane(
        first = {
            CreaseCard(Modifier.fillMaxSize().padding(8.dp), padding = 24.dp) {
                CreaseText("Library", style = CreaseTheme.type.kicker, modifier = Modifier.padding(bottom = 16.dp))
                GalleryGrid(session)
            }
        },
        second = {
            CreaseCard(Modifier.fillMaxSize().padding(8.dp), padding = 24.dp) {
                CreaseText(
                    session.photo.title,
                    style = CreaseTheme.type.title,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                PhotoPreview(session, tilt, tiltAroundX = false, Modifier.weight(1f).fillMaxWidth())
                AdjustmentPanel(session, Modifier.padding(top = 20.dp))
            }
        },
        fallback = TwoPaneFallback.SideBySide,
    )
}

@Composable
private fun CompactEditor(session: EditorSession) {
    CreaseCard(Modifier.fillMaxSize().padding(8.dp), padding = 20.dp) {
        PhotoImage(session.photo, session.adjustments, Modifier.weight(1f).fillMaxWidth())
        Filmstrip(session, Modifier.padding(vertical = 12.dp))
        AdjustmentPanel(session)
    }
}

/** Closed outer display: look at photos, nothing else. */
@Composable
private fun Viewer(session: EditorSession) {
    CreaseCard(Modifier.fillMaxSize().padding(8.dp), padding = 20.dp) {
        PhotoImage(session.photo, session.adjustments, Modifier.weight(1f).fillMaxWidth())
        CreaseText(session.photo.title, style = CreaseTheme.type.title, modifier = Modifier.padding(top = 16.dp))
        Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedPill("Previous", { session.select(session.selectedIndex - 1) }, enabled = session.selectedIndex > 0)
            OutlinedPill(
                "Next",
                { session.select(session.selectedIndex + 1) },
                enabled =
                    session.selectedIndex < session.photos.lastIndex,
            )
        }
    }
}
