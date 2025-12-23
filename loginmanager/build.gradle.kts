plugins {
    // Android library plugin → mandatory for library modules
    alias(libs.plugins.android.library)
    // Kotlin Android plugin → mandatory for Kotlin support
    alias(libs.plugins.kotlin.android)
    // Maven Publish plugin → required for publishing to JitPack
    id("maven-publish")
}

android {
    namespace = "com.yogitechnolabs.loginmanager" // Library ka unique namespace
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro") // Proguard rules for consumers
    }

    buildFeatures {
        viewBinding = true // Optional: allows using viewBinding in library
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Minify false for simplicity, can enable if needed
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
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Media3 for audio/video
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Firebase & Crashlytics
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Other libraries
    implementation(libs.glide.okhttp3.integration)
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.facebook.android:facebook-login:16.3.0")
    implementation("com.github.scribejava:scribejava-apis:8.3.1")
    implementation("com.github.scribejava:scribejava-core:8.3.1")
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                // This ensures the 'release' component of library is published
                from(components["release"])
            }
            // GroupId is GitHub username → mandatory for JitPack
            groupId = "com.github.Romit1914"
            // ArtifactId is library name → will be used in dependency
            artifactId = "loginmanager"
            // Version → should match Git tag pushed to GitHub
            version = "4.1.3"
        }
    }

    repositories {
        maven {
            // URL for JitPack → JitPack automatically picks up this repository
            url = uri("https://jitpack.io")
        }
    }
}