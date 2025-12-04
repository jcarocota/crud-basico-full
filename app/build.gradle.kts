plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
}

android {
    namespace = "com.ebc.crud_basico"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ebc.crud_basico"
        minSdk = 24
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

val navVersion = "2.5.3"
val roomVersion = "2.6.1"
val runtimeLivedataVersion = "1.2.1"
val retrofit = "2.9.0"

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // LiveData + Compose Runtime
    implementation("androidx.compose.runtime:runtime-livedata:$runtimeLivedataVersion")

    // Test monitor
    implementation("androidx.test:monitor:1.5.0")

    // Room compiler con kapt (Kotlin)
    kapt("androidx.room:room-compiler:$roomVersion")


    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:$retrofit")

    // Para que pueda mapear directamente Strings (tu frase geek)
    implementation("com.squareup.retrofit2:converter-scalars:$retrofit")

    implementation("androidx.compose.material:material-icons-extended")
}