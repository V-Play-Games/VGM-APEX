plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.github.v-play-games"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.v-play-games:vjson:2.0.0")
}

kotlin {
    jvmToolchain(21)
}