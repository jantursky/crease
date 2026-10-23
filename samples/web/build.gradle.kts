import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

// The site shows the example files it runs, so the source on the page is always the code that runs.
// They are read at build time into sources.js, next to the fonts and the brand mark.
val siteAssets = layout.buildDirectory.dir("generated/site")
val generateSiteAssets by tasks.registering {
    val examples = layout.projectDirectory.dir("src/wasmJsMain/kotlin/com/jantursky/crease/samples/web/examples")
    val mark = rootProject.layout.projectDirectory.file("design/brand/mark.svg")
    val icon = rootProject.layout.projectDirectory.file("design/brand/icon.svg")
    val fonts = project(":samples:design").layout.projectDirectory.dir("src/commonMain/composeResources/font")
    val version = providers.gradleProperty("VERSION_NAME")
    val output = siteAssets
    inputs.property("version", version)
    inputs.dir(examples)
    inputs.files(mark, icon)
    inputs.dir(fonts)
    outputs.dir(output)
    doLast {
        val out = output.get().asFile.apply { deleteRecursively() }
        out.resolve("fonts").mkdirs()

        fun json(text: String): String =
            buildString {
                append('"')
                text.forEach { c ->
                    when (c) {
                        '"' -> append("\\\"")
                        '\\' -> append("\\\\")
                        '\n' -> append("\\n")
                        '\t' -> append("\\t")
                        '<' -> append("\\u003c")
                        else -> append(c)
                    }
                }
                append('"')
            }
        val files =
            examples.asFile
                .listFiles()
                .orEmpty()
                .filter { it.extension == "kt" }
                .sortedBy { it.name }
        val entries = files.joinToString(",\n") { "  ${json(it.name)}: ${json(it.readText())}" }
        out.resolve("sources.js").writeText(
            "window.CREASE_VERSION = ${json(version.get())};\nwindow.CREASE_SOURCES = {\n$entries\n};\n",
        )
        mark.asFile.copyTo(out.resolve("mark.svg"))
        icon.asFile.copyTo(out.resolve("favicon.svg"))
        fonts.asFile
            .listFiles()
            .orEmpty()
            .forEach { it.copyTo(out.resolve("fonts/${it.name}")) }
    }
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "web"
        browser {
            commonWebpackConfig { outputFileName = "app.js" }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain {
            resources.srcDir(generateSiteAssets)
            dependencies {
                implementation(project(":samples:design"))
                implementation(project(":samples:recipes:shared"))
                implementation(project(":samples:photos:shared"))
            }
        }
    }
}
