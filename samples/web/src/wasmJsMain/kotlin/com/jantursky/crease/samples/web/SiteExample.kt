package com.jantursky.crease.samples.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jantursky.crease.Posture
import com.jantursky.crease.samples.design.DeviceSimulator
import com.jantursky.crease.samples.design.SimulatorState
import com.jantursky.crease.samples.web.examples.AvoidFoldExample
import com.jantursky.crease.samples.web.examples.FoldGeometryExample
import com.jantursky.crease.samples.web.examples.HingeAngleExample
import com.jantursky.crease.samples.web.examples.ListDetailExample
import com.jantursky.crease.samples.web.examples.PostureChangesExample
import com.jantursky.crease.samples.web.examples.ReadingLampExample
import com.jantursky.crease.samples.web.examples.StackedFallbackExample
import com.jantursky.crease.samples.web.examples.TabletopExample

/** The examples on the site, keyed by the `data-example` attribute of their element in index.html. */
internal enum class SiteExample(
    val id: String,
    val posture: Posture,
    val content: @Composable () -> Unit,
    /** All the simulator controls and the fold overlay, for the example about the geometry itself. */
    val detailed: Boolean = false,
) {
    ListDetail("list-detail", Posture.Book, { ListDetailExample() }),
    Tabletop("tabletop", Posture.Tabletop, { TabletopExample() }),
    AvoidFold("avoid-fold", Posture.Book, { AvoidFoldExample() }),
    HingeAngle("hinge-angle", Posture.Book, { HingeAngleExample() }),
    StackedFallback("stacked-fallback", Posture.Flat, { StackedFallbackExample() }),
    PostureChanges("posture-changes", Posture.Flat, { PostureChangesExample() }),
    ReadingLamp("reading-lamp", Posture.Book, { ReadingLampExample() }),
    FoldGeometry("fold-geometry", Posture.Book, { FoldGeometryExample() }, detailed = true),
    ;

    companion object {
        fun byId(id: String?): SiteExample? = entries.firstOrNull { it.id == id }
    }
}

/** One example in a small simulated foldable with the posture and angle controls. */
@Composable
internal fun ExampleFrame(
    example: SiteExample,
    modifier: Modifier = Modifier,
) {
    val state =
        remember {
            SimulatorState().apply {
                posture = example.posture
                overlay = example.detailed
            }
        }
    DeviceSimulator(modifier, state, compact = !example.detailed, content = example.content)
}
