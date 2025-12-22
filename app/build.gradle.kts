//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("org.jetbrains.kotlin.kapt")
//    id("com.google.gms.google-services")
//    id("org.jetbrains.kotlin.plugin.compose")
//}
//
//android {
//    namespace = "com.example.sawitku"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.sawitku"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//    buildFeatures {
//        compose = true
//        viewBinding = true
//        dataBinding = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "2.0.21"
//    }
//}
//
//dependencies {
//    // Firebase
//    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
//    implementation("com.google.firebase:firebase-auth-ktx")
//    implementation("com.google.firebase:firebase-firestore-ktx")
//    implementation("com.google.firebase:firebase-database-ktx")
//    implementation("com.google.firebase:firebase-storage-ktx")
//
//    implementation("com.google.android.gms:play-services-location:21.3.0")
//
//    // AndroidX
//    implementation("androidx.core:core-ktx:1.15.0")
//    implementation("androidx.appcompat:appcompat:1.7.0")
////    implementation("com.google.android.material:material:1.9.0")
//    implementation("com.google.android.material:material:1.12.0")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
//    implementation("androidx.activity:activity-compose:1.9.0")
//    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.recyclerview:recyclerview:1.3.2")
//    implementation("androidx.cardview:cardview:1.0.0")
//
//    // Moshi & Retrofit
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
//    implementation("com.squareup.moshi:moshi:1.15.0")
//    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
//    implementation(libs.volley)
//    implementation(libs.androidx.activity)
//    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
//
//    // Testing
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.2.1")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
//
//    // Compose testing
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
//
//    implementation("com.github.bumptech.glide:glide:4.16.0")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
//
//    // Google Maps API (GoogleMap, SupportMapFragment, dll.)
//    implementation("com.google.android.gms:play-services-maps:18.2.0")
//
//    // FusedLocationProviderClient (Location Services)
//    implementation("com.google.android.gms:play-services-location:21.0.1")
//
//    implementation("androidx.cardview:cardview:1.0.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.sawitku"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sawitku"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Vector drawables are needed for Compose icons
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        // 2. Enable Compose in build features
        compose = true
        // You can keep viewBinding if you have a hybrid app
        viewBinding = true
    }

    composeOptions {
        // 3. Set the Kotlin Compiler Extension version
        // This version should be compatible with your Kotlin plugin version (1.9.24)
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core & Appcompat
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")

    // Layouts (for traditional views)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // JETPACK COMPOSE DEPENDENCIES
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.ui.test)
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // Google Maps & Location
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Moshi + Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Volley
    implementation("com.android.volley:volley:1.2.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose Testing
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

}