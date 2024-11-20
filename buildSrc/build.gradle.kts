plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlinx.serialization)
}

repositories {
    mavenCentral()
    maven { setUrl("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation(libs.kotlinx.json)
}