plugins {
    kotlin("jvm")
}

group = "net.vplaygames"
version = "0.0.1"

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jsoup)
    implementation(libs.vjson)
}
