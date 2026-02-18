import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "net.vplaygames.apex"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.vplaygames.apex"
        minSdk = 29
        targetSdk = 36
        versionCode = 7
        versionName = "0.1.5"
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
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

dependencies {
    implementation(libs.core.ktx)

    // Jetpack Compose
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)

    // Splash Screen
    implementation(libs.splash.screen)

    // Preferences
    implementation(libs.datastore.preferences)

    // Navigation
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
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

    // Validation
    implementation(libs.konform)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.concurrent.futures.ktx)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.compose.shimmer)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))

    // Auth
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Ktor Client
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serialization.json)
}
