plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.busapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.busapp"
        minSdk = 25
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))
    implementation("com.google.firebase:firebase-analytics")



        implementation ("com.google.firebase:firebase-auth")
        implementation ("com.google.firebase:firebase-firestore")
        implementation ("com.google.firebase:firebase-database")
        implementation ("com.google.android.gms:play-services-maps:18.2.0")
        implementation ("com.google.android.gms:play-services-location:21.0.1")
        implementation("com.google.maps.android:android-maps-utils:2.4.0")


}