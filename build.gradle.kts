import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform") version "2.4.10"
}

repositories {
    mavenCentral()
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    js(IR) {
        browser {
            binaries.executable()
        }
    }

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react:2026.9.0-19.2.8")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:2026.9.0-19.2.8")
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
        }
    }
}