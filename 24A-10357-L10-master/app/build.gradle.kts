import org.gradle.internal.impldep.org.bouncycastle.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "dev.tomco.my24a_10357_l10"
    compileSdk = 34

    buildFeatures{
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "dev.tomco.my24a_10357_l10"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${properties["GoogleMapsApiKey"]}\"")
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = properties["GoogleMapsApiKey"]?.toString() ?: ""
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.firebase.authentication.ui)
    implementation("com.google.firebase:firebase-database:20.0.3")
    implementation("com.google.gms:google-services:4.3.10")

    // due to changes in firebase auth ui use this to allow login:
    implementation(libs.gms.play.services.auth)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
}