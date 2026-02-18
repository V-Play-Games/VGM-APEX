import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
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
    implementation(libs.androidx.core.ktx)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)

    // Splash Screen
    implementation(libs.androidx.core.splashScreen)

    // Preferences
    implementation(libs.androidx.datastorePreferences)

    // Navigation
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serializationJson)

    // Material UI
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsExtended)

    // Media3
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // JSON
    implementation(libs.vjson)

    // Validation
    implementation(libs.konform)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.networkOkhttp)
    implementation(libs.androidx.concurrentFuturesKtx)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.compose.shimmer)

    // Firebase BOM
    implementation(platform(libs.google.firebase.bom))

    // Auth
    implementation(libs.google.firebase.auth)
    implementation(libs.google.playServicesAuth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.playServicesAuth)
    implementation(libs.google.googleid)

    // Ktor Client
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serializationJson)
}
