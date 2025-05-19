plugins {
    alias(libs.plugins.android.application)
}

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.pocketforager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pocketforager"
        minSdk = 28
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    tasks.withType<Test> {
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
        }
    }
}

dependencies {

    implementation(libs.room.common.jvm)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    val room_version = "2.7.1" // for local database

    val navVersion = "2.8.8" // for basic navigation

    //for basic navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //for local database
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // for google maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    //test implementation
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.5.2")
    testImplementation("androidx.test:core:1.5.0")


    // Picasso for showing plant picture in details page
    implementation (libs.picasso)
    implementation (libs.picasso.v271828)

}