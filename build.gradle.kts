plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.catdogapp"
    compileSdk = 34

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    defaultConfig {
        applicationId = "com.example.catdogapp"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    // Kotlin standard library
    implementation(libs.kotlin.stdlib)

    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.runtime.livedata)

    // Optional for Compose testing
    androidTestImplementation(libs.ui.test.junit4)

    // AndroidX dependencies
    implementation(libs.androidx.core.ktx.v170)
    implementation(libs.androidx.appcompat)

    // Google Material Design components
    implementation(libs.material)

    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)

    // Kotlin standard library
    implementation(libs.kotlin.stdlib)

    // Jetpack Compose Dependencies
    implementation(libs.androidx.ui.v150) // Main UI elements
    implementation(libs.androidx.material.v150) // Material Design elements like Scaffold, MaterialTheme
    implementation(libs.androidx.ui.tooling.preview.v150) // For previewing Compose UI components
    implementation(libs.androidx.activity.compose.v160) // Activity for Compose

    // AndroidX and other libraries
    implementation(libs.androidx.core.ktx.v1131)
    implementation(libs.androidx.appcompat.v161)

    // Google Material Design components
    implementation(libs.material.v190)

    // Unit Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core.v361)

    // Unit Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)
}
