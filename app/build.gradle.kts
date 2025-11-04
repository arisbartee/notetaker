plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.example.notetaker"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.notetaker"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

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
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Navigation 3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.bundles.android.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.debug)
}

ktlint {
    version.set("1.0.1")

    // Android-specific configuration
    android.set(true)

    // Use .editorconfig settings
    ignoreFailures.set(false)

    // Include specific source sets
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
        include("**/kotlin/**")
        include("**/java/**")
    }

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }

    // Enable colored output
    coloredOutput.set(true)
}

// Custom tasks for easier ktlint usage
tasks.register("lintKotlin") {
    dependsOn("ktlintCheck")
    group = "verification"
    description = "Run ktlint checks on Kotlin code"
}

tasks.register("formatKotlin") {
    dependsOn("ktlintFormat")
    group = "formatting"
    description = "Format Kotlin code with ktlint"
}

// Hook ktlint into the build process
tasks.named("preBuild") {
    dependsOn("ktlintCheck")
}

// Hook ktlint format into the clean task for convenience
tasks.named("clean") {
    finalizedBy("ktlintFormat")
}
