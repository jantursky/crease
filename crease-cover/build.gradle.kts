import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.binary.compatibility.validator)
}

kotlin {
    explicitApi()

    android {
        namespace = "com.jantursky.crease.cover"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        withHostTest { isIncludeAndroidResources = true }
    }

    jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.ui)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.window)
        }
        getByName("androidHostTest").dependencies {
            implementation(kotlin("test"))
            implementation(libs.junit)
            implementation(libs.robolectric)
            implementation(libs.androidx.window.testing)
        }
        jvmTest.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui.test)
            implementation(compose.desktop.currentOs)
        }
    }
}

tasks.withType<Test>().matching { it.name == "testAndroidHostTest" }.configureEach {
    // Robolectric's newest Android sandbox builds file descriptors through JDK internals that recent JDKs
    // no longer export.
    jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
}

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib { enabled = true }
}

kover {
    reports {
        filters {
            excludes {
                // Platform glue is exercised on devices and simulators, not in host tests.
                packages("com.jantursky.crease.cover.platform")
            }
        }
        verify {
            rule {
                bound { minValue = 90 }
                bound {
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH
                    minValue = 78
                }
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    // CI passes the key as ORG_GRADLE_PROJECT_signingInMemoryKey; local builds publish unsigned.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) signAllPublications()
    coordinates(artifactId = "crease-cover")
    pom {
        name.set("Crease Cover")
        description.set(
            "Show a composable on the outer display of a foldable from common Compose code, " +
                "on Android rear display areas and the iPhone Duo.",
        )
        inceptionYear.set("2026")
        url.set("https://github.com/jantursky/crease")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("jantursky")
                name.set("Jan Tursky")
                email.set("jan.tursky@gmail.com")
                url.set("https://jantursky.com")
            }
        }
        scm {
            url.set("https://github.com/jantursky/crease")
            connection.set("scm:git:https://github.com/jantursky/crease.git")
            developerConnection.set("scm:git:ssh://git@github.com/jantursky/crease.git")
        }
    }
}

// The wasm test run covers commonTest only, so it doesn't need the Skiko runtime that this check asks for.
tasks.named { it == "checkComposeUiTestConfigurationForWasmJs" }.configureEach { enabled = false }
