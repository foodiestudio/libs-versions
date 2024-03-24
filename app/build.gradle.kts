@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
}

android {
    namespace = "com.example.app"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

// > ./gradlew dependencies | grep "releaseCompileClasspath"
// check versions: kotlin, compose
dependencies {
    implementation(libs.bundles.network)
    implementation(libs.bundles.jetpack)
    implementation(platform(libs.okhttp.bom))
    implementation(platform(libs.coil.bom))
    implementation(platform(libs.koin.bom))
    implementation(libs.bundles.others)

    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose.core)
    implementation(libs.bundles.jetpack.compose)
    implementation(libs.bundles.accompanist)

    implementation(libs.bundles.internal)

    debugImplementation(libs.bundles.debug)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.tests)
}