plugins {
    kotlin("jvm")
    alias(libs.plugins.ktor)
}

group = "com.github.v-play-games"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // Ktor
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)

    // Firebase Admin SDK
    implementation(libs.firebase.admin)
}
