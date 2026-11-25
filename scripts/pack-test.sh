#!/usr/bin/env bash
# Publishes the library to Maven Local and compiles a fresh consumer project against it,
# so a broken POM, a missing variant or a leaked internal type fails here, not for users.
set -euo pipefail

root=$(cd "$(dirname "$0")/.." && pwd)
version=$(sed -n 's/^VERSION_NAME=//p' "$root/gradle.properties")
kotlin=$(sed -n 's/^kotlin = "\(.*\)"/\1/p' "$root/gradle/libs.versions.toml")
compose=$(sed -n 's/^compose = "\(.*\)"/\1/p' "$root/gradle/libs.versions.toml")
consumer=$(mktemp -d)
trap 'rm -rf "$consumer"' EXIT

"$root/gradlew" -p "$root" :crease:publishToMavenLocal :crease-cover:publishToMavenLocal :crease-test:publishToMavenLocal :crease-adaptive:publishToMavenLocal --quiet

mkdir -p "$consumer/src/jvmMain/kotlin" "$consumer/src/jvmTest/kotlin"
cat > "$consumer/settings.gradle.kts" <<'EOF'
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal { content { includeGroup("io.github.jantursky") } }
        google()
        mavenCentral()
    }
}

rootProject.name = "consumer"
EOF
cat > "$consumer/build.gradle.kts" <<EOF
plugins {
    kotlin("multiplatform") version "$kotlin"
    id("org.jetbrains.kotlin.plugin.compose") version "$kotlin"
    id("org.jetbrains.compose") version "$compose"
}

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation("io.github.jantursky:crease:$version")
            implementation("io.github.jantursky:crease-cover:$version")
            implementation("io.github.jantursky:crease-adaptive:$version")
        }
        jvmTest.dependencies {
            implementation("io.github.jantursky:crease-test:$version")
            implementation(kotlin("test"))
        }
    }
}
EOF
cat > "$consumer/src/jvmMain/kotlin/Consumer.kt" <<'EOF'
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.jantursky.crease.FoldAvoidingPopupPositionProvider
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture
import com.jantursky.crease.ProvideFoldState
import com.jantursky.crease.adaptive.rememberFoldLayoutClass
import com.jantursky.crease.adaptive.rememberWindowAdaptiveInfo
import com.jantursky.crease.avoidFold
import com.jantursky.crease.cover.CoverDisplay
import com.jantursky.crease.cover.CoverDisplayState
import com.jantursky.crease.cover.ProvideCoverDisplayState
import com.jantursky.crease.cover.rememberCoverDisplayState
import com.jantursky.crease.rememberFoldAvoidingPopupPositionProvider
import com.jantursky.crease.rememberFoldState

private object Anchored : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = anchorBounds.topLeft
}

@Composable
fun Screen(content: @Composable () -> Unit) {
    ProvideFoldState(FoldState.book(Size(1800f, 1200f))) {
        check(rememberFoldState().posture == Posture.Book)
        FoldAwareTwoPane(first = content, second = content, modifier = Modifier.avoidFold())
        check(rememberWindowAdaptiveInfo().windowPosture.hingeList.size == 1)
        check(rememberFoldLayoutClass().posture == Posture.Book)
        check(rememberFoldAvoidingPopupPositionProvider(Anchored) != null)
        check(FoldAvoidingPopupPositionProvider(Anchored) { null } != null)
    }
    ProvideCoverDisplayState(CoverDisplayState.Available) {
        check(rememberCoverDisplayState() == CoverDisplayState.Available)
        CoverDisplay(content = content)
    }
}
EOF
cat > "$consumer/src/jvmTest/kotlin/ConsumerTest.kt" <<'EOF'
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.jantursky.crease.Posture
import com.jantursky.crease.test.FoldStatePresets
import com.jantursky.crease.test.setContentWithFold
import kotlin.test.Test

class ConsumerTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun folds() = runComposeUiTest {
        val fold = setContentWithFold(FoldStatePresets.Seamless.flat()) { Screen {} }
        fold.moveTo(Posture.Tabletop, Size(1800f, 1200f))
        fold.play(listOf(FoldStatePresets.Divided.book()))
    }
}
EOF

"$root/gradlew" -p "$consumer" compileKotlinJvm compileTestKotlinJvm --quiet
echo "Consumer compiled against io.github.jantursky:crease:$version, crease-cover, crease-test and crease-adaptive"
