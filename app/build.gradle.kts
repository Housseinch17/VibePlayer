plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.example.vibeplayer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.vibeplayer"
        minSdk = 24
        targetSdk = 36
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
room {
    schemaDirectory("$projectDir/schemas")
}



dependencies {
    //Androidx ktx
    implementation(libs.bundles.androidx.ktx)

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.junit)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    //Android test
    androidTestImplementation(libs.bundles.android.test)

    //Compose debug
    debugImplementation(libs.bundles.compose.debug)

    //Splash screen
    implementation(libs.androidx.core.splashscreen)

    //Coil
    implementation(libs.coil.compose)

    //Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    //Compose all dependencies
    implementation(libs.bundles.compose)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //Koin
    implementation(libs.bundles.koin.compose)

    //Timber
    implementation(libs.timber)

}