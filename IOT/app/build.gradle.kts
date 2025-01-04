plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.iot.iot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iot.iot"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")
    implementation("com.itextpdf:itext7-core:7.2.2")
    implementation("com.alibaba:easyexcel:3.0.5")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(group = "com.airbnb.android", name = "lottie", version = "6.0.0")
    implementation(libs.datastore.preferences)
}

