import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Android entry point. Kotlin support is built into AGP 9, so no kotlin-android plugin.
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}

// Emulator default; for a physical device pass the PC's LAN address:
//   .\gradlew.bat :androidApp:installDebug -PapiBaseUrl=http://192.168.1.20:3000/api/v1
val apiBaseUrl = (findProperty("apiBaseUrl") as String?) ?: "http://10.0.2.2:3000/api/v1"

android {
    namespace = "dk.careerexplore.poc"
    compileSdk = 37

    defaultConfig {
        applicationId = "dk.careerexplore.poc"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.3.0"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
}
