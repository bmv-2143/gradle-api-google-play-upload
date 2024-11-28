import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import com.google.api.services.androidpublisher.model.LocalizedText
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.triplet.play)
    id("GooglePlayPublishPlugin")
}

val keystorePropertiesFile = rootProject.file("credentials/keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "net.humblegames.hw_gradle_api"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.humblegames.hw_gradle_api"
        minSdk = 26
        targetSdk = 34
        versionCode = 11
        versionName = "1.0.10"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val releaseSigningConfig = "releaseSigningConfig"

    signingConfigs {
        create(releaseSigningConfig) {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName(releaseSigningConfig)
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
        }
    }
}

// My implementation of the assignment:
googlePlayPublishExtension {
    trackToPublish.set("internal")
    releaseName.set(getReleaseName())
    rolloutPercentage.set(0.10)
    status.set("inProgress")
    releaseNotes.set(
        listOf(
            LocalizedText().setLanguage("en-US")
                .setText("Another release notes from Gradle 4"),
        )
    )
}

// Not required by the assignment: upload via third party plugin:
// https://github.com/Triple-T/gradle-play-publisher
play {
    defaultToAppBundles.set(true)
    releaseName.set(getReleaseName())
    track.set("internal")
    defaultToAppBundles.set(true)
    releaseStatus.set(ReleaseStatus.DRAFT)
    serviceAccountCredentials.set(file("../credentials/gradle-upload-apk-to-play.json"))
}

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
}

fun getTimestamp(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd-HHmm")
    return dateFormat.format(Date())
}

fun getReleaseName(): String =
    project.rootProject.name +
        "-vc${android.defaultConfig.versionCode}" +
        "-vn${android.defaultConfig.versionName}" +
        "-${getTimestamp()}"
