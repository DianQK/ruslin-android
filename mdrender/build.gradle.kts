@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.dianqk.mdrender"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()
    ndkVersion = libs.versions.ndkVersion.get()
    buildToolsVersion = libs.versions.buildToolsVersion.get()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()
//        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
//        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn -Xjvm-default=all"
    }
}

dependencies {

    // Replying on uniffi is not a good choice
    implementation(project(":uniffi"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}