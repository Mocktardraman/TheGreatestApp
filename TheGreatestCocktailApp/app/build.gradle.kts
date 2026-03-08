plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "fr.isen.mocktar.thegreatestcocktailapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.isen.mocktar.thegreatestcocktailapp"
        minSdk = 24
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
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Active Compose
    buildFeatures {
        compose = true
    }

    // Version du compiler extension Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    // ⚠️ Sur Kotlin DSL, place bien kotlinOptions à l'intérieur du bloc android
    kotlinOptions {
        jvmTarget = "17"
        // (Optionnel) Pour les lambdas
        freeCompilerArgs += listOf(
            "-Xjvm-default=all"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// ✅ Alternative moderne si tu préfères (remplace le bloc kotlinOptions ci‑dessus par ceci) :
// kotlin {
//     jvmToolchain(17)
// }

dependencies {
    // Compose BOM = synchronise les versions Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose + Activity
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Material 3 (Compose)
    implementation("androidx.compose.material3:material3:1.3.1")
    
    // Material Components (XML Themes)
    implementation("com.google.android.material:material:1.12.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Lifecycle + Coroutines
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Retrofit + Gson + OkHttp logging
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coil (images)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Icônes Material (pour Icons.Filled / Icons.Outlined)
    implementation("androidx.compose.material:material-icons-extended")

    // Tests (optionnels)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}