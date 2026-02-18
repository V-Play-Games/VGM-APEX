plugins {
    kotlin("jvm")
}

group = "net.vplaygames"
version = "0.0.1"

dependencies {
    implementation(libs.vjson)
}

kotlin {
    jvmToolchain(25)
}