plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

gradlePlugin {
    plugins.register("GooglePlayPublishPlugin") {
        id = "net.humblegames.google-play-publish"
        implementationClass = "net.humblegames.gradle.GooglePlayPublishPlugin"
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.0")

    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20241016-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.api-client:google-api-client:1.32.2")
    implementation("com.google.http-client:google-http-client-gson:1.44.1")
}
