import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    wasmJs {
        moduleName = "bikeshare"
        browser {
            commonWebpackConfig {
                outputFileName = "bikeshare.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    open = mapOf(
                        "app" to mapOf(
                            "name" to "google chrome canary",
                            "arguments" to listOf("--js-flags=--experimental-wasm-gc ")
                        )
                    ),
                    static = (devServer?.static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/common/")
                        add(project.rootDir.path + "/nonAndroidMain/")
                        add(project.rootDir.path + "/web/")
                    },
                    )
            }
        }
        binaries.executable()
//        applyBinaryen()
    }

    sourceSets {
        @OptIn(ExperimentalComposeLibrary::class)
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                implementation("io.ktor:ktor-client-core:3.0.0-wasm1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm1")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm1")
            }
        }
    }
}

compose.experimental {
    web.application {}
}

compose {
    kotlinCompilerPlugin.set("1.5.4")
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin}")
}