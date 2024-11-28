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
        id = "GooglePlayPublishPlugin"
        implementationClass = "net.humblegames.gradle.GooglePlayPublishPlugin"
    }
}

//open class PublishingConfig {
//    var track: String = "internal"
//    var rolloutPercentage: Double = 1.0
//    var releaseNotes: Map<String, String> = mapOf()
//}
//
//extensions.create("publishingConfig", PublishingConfig::class)

dependencies {
    implementation("com.android.tools.build:gradle:8.2.0")

    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20241016-2.0.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.api-client:google-api-client:1.32.2")
    implementation("com.google.api-client:google-api-client-jackson2:1.32.2")
    implementation("com.google.http-client:google-http-client-gson:1.44.1")
}
