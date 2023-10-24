plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.promecarus.storyapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.promecarus.storyapp"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("Boolean", "DEBUG", "true")
        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    // default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // activity
    implementation("androidx.activity:activity-ktx:1.8.0")

    // datastore
    implementation("androidx.datastore:datastore-preferences:1.1.0-alpha05")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // gms
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // okhttp
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // swiperefreshlayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
}
