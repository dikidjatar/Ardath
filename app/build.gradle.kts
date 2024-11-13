plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.gms.google.services)
}

sealed class Version(
    val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int
) {
    abstract fun toVersionName(): String
    class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
        Version(versionMajor, versionMinor, versionPatch) {
            override fun toVersionName(): String = "${versionMajor}.${versionMinor}.${versionPatch}"
        }
}

val currentVersion = Version.Stable(
    versionMajor = 1,
    versionMinor = 0,
    versionPatch = 0
)

android {
    namespace = "com.djatar.ardath"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.djatar.ardath"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = currentVersion.toVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    // coil (image loader)
    implementation(libs.coil.compose)

    // OkHttp
    implementation(libs.okHttp)

    // material icon
    implementation(libs.material.icon.extended)

    // Hilt DI (Dependencies Injection)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))

    // Firebase database
    implementation(libs.firebase.database)
    // Firebase authentication
    implementation(libs.firebase.authentication)
    // Firebase storage
    implementation(libs.firebase.storage)
    // Firebase messaging
    implementation(libs.firebase.messaging)
    // Firebase analytics
    implementation(libs.firebase.analytics)
    // Google oauth2
    implementation (libs.google.auth.library.oauth2.http)

    // Android navigation compose
    implementation(libs.androidx.navigation.compose)

    // Accompanist
    implementation(libs.accompanist.permission)
    implementation(libs.accompanist.placeholder)
    implementation(libs.accompanist.swiperefresh)

    // key-value
    implementation(libs.mmkv)
    // fuzzywuzzy
    implementation(libs.fuzzywuzzy)

    // Material
    implementation(libs.androidx.material)

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
}