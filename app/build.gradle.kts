import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
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


}


dependencies {

    implementation(libs.room.common.jvm)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    val room_version = "2.7.1" // for local database



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
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // for google maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    //test implementation
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.5.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation ("org.mockito:mockito-core:4.+")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.json:json:20230227")


    // Picasso for showing plant picture in details page
    implementation (libs.picasso)
    implementation (libs.picasso.v271828)


}

jacoco { toolVersion = "0.8.12" }

val debugJava   =
    "$buildDir/intermediates/javac/debug/compileDebugJavaWithJavac/classes"
val debugKotlin = "$buildDir/does/not/exist"


val fileFilter = listOf(
    "**/R.class", "**/R$*.class",
    "**/BuildConfig.*", "**/Manifest*.*",
    "**/*Test*.*", "android/**/*.*"
)

val includePkgs = listOf(
    "com/example/pocketforager/location/**",
    "com/example/pocketforager/utils/**",
    "com/example/pocketforager/model/**"
)

tasks.register<JacocoReport>("jacocoUnitTestReport") {
    dependsOn("testDebugUnitTest")

    reports { xml.required.set(true); html.required.set(true) }

    classDirectories.setFrom(
        files(
            fileTree(debugJava) {
                include(includePkgs)
                exclude(fileFilter)
            },
            fileTree(debugKotlin) {
                include(includePkgs)
                exclude(fileFilter)
            }
        )
    )

    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir) { include("jacoco/testDebugUnitTest.exec") })
}

tasks.register<JacocoCoverageVerification>("jacocoCoverageCheck") {
    dependsOn("jacocoUnitTestReport")

    classDirectories.setFrom(tasks.named("jacocoUnitTestReport").map {
        (it as JacocoReport).classDirectories
    })
    sourceDirectories.setFrom(tasks.named("jacocoUnitTestReport").map {
        (it as JacocoReport).sourceDirectories
    })
    executionData.setFrom(tasks.named("jacocoUnitTestReport").map {
        (it as JacocoReport).executionData
    })


    violationRules {
        rule {
            limit {
                counter = "LINE"
                value   = "COVEREDRATIO"
                minimum = "0.03".toBigDecimal()
            }
        }
    }
}

tasks.named("check") { dependsOn("jacocoCoverageCheck") }

tasks.withType<Test>().configureEach {

    jvmArgs("--add-opens=java.base/jdk.internal.reflect=ALL-UNNAMED")
    testLogging { events("PASSED", "FAILED", "SKIPPED") }
}

tasks.register("locateDebugClasses") {
    doLast {
        println("─── CLASS DIRECTORIES UNDER build/ ───")
        fileTree(buildDir) {
            include("**/*.class")
        }.files
            .map { it.parentFile }
            .distinct()
            .sortedBy { it.path }
            .forEach { println(it.relativeTo(buildDir)) }
    }
}


