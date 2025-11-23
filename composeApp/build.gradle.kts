import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Navigation 3
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.material3.adaptive.navigation3)
            implementation(libs.kotlinx.serialization.core)

            // Lottie
            implementation(libs.lottie)

            // FONT
            implementation(libs.androidx.ui.text.google.fonts)

            // Adaptive
            // Haupt-Library für adaptive Komponenten
            implementation(libs.androidx.adaptive)

            // Speziell für adaptive Layouts
            implementation(libs.androidx.adaptive.layout)

            // Für die adaptive Navigation
            implementation(libs.androidx.adaptive.navigation)

            implementation(libs.androidx.material3.adaptive.navigation.suite)

            // If you're using Material 3, use compose-placeholder-material3
            implementation(libs.compose.placeholder.material3)

            // Coil
            implementation(libs.coil.compose)

            // Gampose https://github.com/ezlifeSol/gampose
            //implementation(libs.gampose)

            // Yaml
            implementation(libs.jackson.dataformat.yaml)
            implementation(libs.jackson.module.kotlin)

            implementation(project.dependencies.platform("androidx.compose:compose-bom:2025.11.00"))
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.1.0"))

            implementation(project.dependencies.platform(libs.sentry.bom))

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.datastore.core.android)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.androidx.room.ktx)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.room.runtime)

            implementation(libs.converter.moshi)

            implementation(libs.firebase.analytics)

            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.logging.interceptor)

            implementation(libs.material3)
            implementation(libs.material3.window.size)

            implementation(libs.moshi.adapters)
            implementation(libs.moshi.kotlin)

            implementation(libs.okhttp)

            implementation(libs.retrofit)

            implementation(libs.sentry.android)
            implementation(libs.sentry.compose.android)

            implementation(libs.androidx.core.splashscreen)

            implementation(libs.kotlin.metadata.jvm)

            implementation(libs.androidx.material.icons.extended)
            implementation(libs.androidx.ui.tooling)
            implementation(libs.androidx.ui)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "de.ljz.questify"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        val major = 0
        val minor = 11
        val patch = 0

        versionName = "$major.$minor.$patch"
        versionCode = (major * 10000) + (minor * 100) + patch

        applicationId = "de.ljz.questify"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

