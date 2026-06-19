plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.devagarwal.ads_sdk_integration_android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devagarwal.ads_sdk_integration_android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")

    // Local Jars
    implementation(files("libs/osmos-release-v2.6.0.jar", "libs/omid_1.5.5.jar"))

    // OkHttp for direct URL pings
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    "kapt"(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Navigation Compose
    implementation(libs.navigation.compose)

    // Coil for Image Loading
    implementation(libs.coil.compose)

    // WorkManager (required by Osmos SDK)
    implementation(libs.androidx.work.runtime.ktx)

    // Play Services Ads Identifier (required by Osmos SDK)
    implementation(libs.play.services.ads.identifier)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}