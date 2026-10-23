package com.jantursky.crease.samples.photos

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.jantursky.crease.samples.design.CreaseShapes
import com.jantursky.crease.samples.design.CreaseText
import com.jantursky.crease.samples.design.CreaseTheme

private const val MAX_TILT = 8f

/** Lean of the preview in degrees: nothing at 90°, leaning back as the hinge opens, forward as it closes. */
internal fun tiltDegrees(angle: Float?): Float =
    if (angle ==
        null
    ) {
        0f
    } else {
        ((angle - 90f) / 90f).coerceIn(-1f, 1f) * MAX_TILT
    }

@Composable
fun PhotoImage(
    photo: Photo,
    adjustments: Adjustments,
    modifier: Modifier = Modifier,
    shape: Shape = CreaseShapes.floating,
) {
    Image(
        bitmap = photo.image,
        contentDescription = photo.title,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.colorMatrix(adjustments.toColorMatrix()),
        modifier = modifier.clip(shape),
    )
}

/** The edited photo. [tilt] leans it around the X axis (tabletop) or the Y axis (book). */
@Composable
fun PhotoPreview(
    session: EditorSession,
    tilt: Float,
    tiltAroundX: Boolean,
    modifier: Modifier = Modifier,
) {
    val distance = 12f * LocalDensity.current.density
    PhotoImage(
        photo = session.photo,
        adjustments = session.adjustments,
        shape = CreaseShapes.card,
        modifier =
            modifier.graphicsLayer {
                cameraDistance = distance
                if (tiltAroundX) rotationX = tilt else rotationY = tilt
            },
    )
}

@Composable
private fun Thumbnail(
    session: EditorSession,
    index: Int,
    modifier: Modifier = Modifier,
) {
    val colors = CreaseTheme.colors
    val selected = index == session.selectedIndex
    val photo = session.photos[index]
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PhotoImage(
            photo = photo,
            adjustments = session.adjustmentsOf(index),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 2f)
                    .border(
                        if (selected) 2.dp else 1.dp,
                        if (selected) colors.ink else colors.control,
                        CreaseShapes.floating,
                    ).clickable(role = Role.Button) { session.select(index) },
        )
        CreaseText(
            photo.title,
            style = CreaseTheme.type.caption,
            color = if (selected) colors.ink else colors.slate,
            maxLines = 1,
        )
    }
}

@Composable
fun GalleryGrid(
    session: EditorSession,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(session.photos) { index, _ -> Thumbnail(session, index) }
    }
}

@Composable
fun Filmstrip(
    session: EditorSession,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(
            session.photos,
        ) { index, _ -> Thumbnail(session, index, Modifier.width(110.dp).padding(vertical = 2.dp)) }
    }
}
