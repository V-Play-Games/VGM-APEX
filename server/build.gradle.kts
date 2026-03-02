plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    application
}

group = "net.vplaygames.apex"
version = "1.0.0"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)

    // Ktor Core
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