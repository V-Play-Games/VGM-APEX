plugins {
    kotlin("jvm")
}

group = "com.github.v-play-games"
version = "0.0.1"

dependencies {
    implementation(libs.kotlinx.coroutines.core);
    implementation(libs.jsoup)
    implementation(libs.vjson)
}
