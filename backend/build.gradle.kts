plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "net.vplaygames"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // Ktor Core
    implementation(libs.ktor.server.configYaml)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty)
    implementation(libs.logbackClassic)

    // Ktor Plugins
    implementation(libs.kotlinx.serializationJson)
    implementation(libs.ktor.serializationJson)
    implementation(libs.ktor.server.callLogging)
    implementation(libs.ktor.server.contentNegotiation)

    // Firebase Admin SDK
    implementation(libs.google.firebase.admin)

    // Database
    implementation(libs.mongodb.driver)

    // Testing
    testImplementation(libs.ktor.server.testHost)
}
