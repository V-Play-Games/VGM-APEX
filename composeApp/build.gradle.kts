import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain.dependencies {
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

            implementation(libs.compose.shimmer)

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
        commonMain.dependencies {
            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

android {
    namespace = "net.vplaygames.apex"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.vplaygames.apex"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
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

dependencies {
    // Firebase BOM
    implementation(platform(libs.google.firebase.bom))

    // Use target-specific KSP configuration for Kotlin Multiplatform
    add("kspAndroid", libs.hilt.compiler)
}
