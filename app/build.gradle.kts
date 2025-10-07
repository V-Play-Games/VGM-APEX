import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "net.vpg.apex"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.vpg.apex"
        minSdk = 29
        targetSdk = 36
        versionCode = 5
        versionName = "0.1.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.core.ktx)

    // Jetpack Compose
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)

    // Preferences
    implementation(libs.datastore.preferences)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Material UI
    implementation(libs.material3)
    implementation(libs.material.icons.extended)

    // Media3
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // JSON
    implementation(libs.vjson)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.concurrent.futures.ktx)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.compose.shimmer)
}
