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

open class PublishingConfig {
    var track: String = "internal"
    var rolloutPercentage: Double = 1.0
    var releaseNotes: Map<String, String> = mapOf()
}

extensions.create("publishingConfig", PublishingConfig::class)
