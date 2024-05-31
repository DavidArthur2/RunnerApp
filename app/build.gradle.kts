plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.festipay.runnerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.festipay.runnerapp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "0.9.5"

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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage:20.0.1")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.firebaseui:firebase-ui-storage:8.0.0")

    implementation("androidx.camera:camera-core:1.0.2")
    implementation("androidx.camera:camera-camera2:1.0.2")
    implementation("androidx.camera:camera-lifecycle:1.0.2")
    implementation("androidx.camera:camera-view:1.0.0-alpha24")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.android.gms:play-services-code-scanner:16.0.0")


}