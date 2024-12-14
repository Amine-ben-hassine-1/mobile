plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Dépendances AndroidX
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.constraintlayout)

    // Dépendances Google Material
    implementation(libs.google.material)

    // Dépendances Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.activity)
    implementation(libs.core.ktx)

    // Dépendances Apache POI pour la lecture des fichiers Excel
    implementation(libs.poi)
    implementation(libs.poi.ooxml)

    // Dépendances iText pour la lecture des fichiers PDF
    implementation(libs.itext7.core)
    implementation(libs.firebase.storage)

    // Dépendances pour les tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Power BI (via WebView intégré)
    implementation(libs.androidx.webkit)
    implementation(libs.mpAndroidChart)


}
