plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
//    id("com.google.devtools.ksp")
}

android {
    namespace = "net.vpg.apex"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.vpg.apex"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // Core Android Libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")

    // Jetpack Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    // Material Design Components
    implementation("com.google.android.material:material:1.12.0")

    // Glide for Image Loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
//    ksp("com.github.bumptech.glide:compiler:4.15.1")

    // ExoPlayer for Music Streaming
    implementation("androidx.media3:media3-exoplayer:1.6.1")
    implementation("androidx.media3:media3-ui:1.6.1")

    // Use the Firebase BoM to manage versions
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add dependencies for the Firebase products you need
//    implementation("com.google.firebase:firebase-analytics-ktx") // Firebase Analytics
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication
//    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore Database
//    implementation("com.google.firebase:firebase-storage-ktx") // Cloud Storage
}