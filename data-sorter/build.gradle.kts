plugins {
    kotlin("jvm")
}

group = "com.github.v-play-games"
version = "0.0.1"

dependencies {
    implementation(libs.vjson)
}

kotlin {
    jvmToolchain(25)
}