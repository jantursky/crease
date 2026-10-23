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

"$root/gradlew" -p "$root" :crease:publishToMavenLocal --quiet

mkdir -p "$consumer/src/jvmMain/kotlin"
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
        }
    }
}
EOF
cat > "$consumer/src/jvmMain/kotlin/Consumer.kt" <<'EOF'
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.jantursky.crease.FoldAwareTwoPane
import com.jantursky.crease.FoldState
import com.jantursky.crease.Posture
import com.jantursky.crease.ProvideFoldState
import com.jantursky.crease.avoidFold
import com.jantursky.crease.rememberFoldState

@Composable
fun Screen(content: @Composable () -> Unit) {
    ProvideFoldState(FoldState.book(Size(1800f, 1200f))) {
        check(rememberFoldState().posture == Posture.Book)
        FoldAwareTwoPane(first = content, second = content, modifier = Modifier.avoidFold())
    }
}
EOF

"$root/gradlew" -p "$consumer" compileKotlinJvm --quiet
echo "Consumer compiled against io.github.jantursky:crease:$version"
