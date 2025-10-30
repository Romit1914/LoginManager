plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.yogitechnolabs.loginmanager"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.glide.okhttp3.integration)
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.facebook.android:facebook-login:16.3.0")
    implementation("com.github.scribejava:scribejava-apis:8.3.1")
    implementation("com.github.scribejava:scribejava-core:8.3.1")

    implementation("com.airbnb.android:lottie:6.4.0")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            afterEvaluate {
                from(components["release"])
            }
            groupId = "com.github.Romit1914"
            artifactId = "loginmanager"
            version = "1.8.9"
        }
    }

    repositories {
        maven {
            url = uri("https://jitpack.io") // JitPack automatically picks up
        }
    }
}