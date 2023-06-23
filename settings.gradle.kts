pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "github-publish") {
                useModule("com.github.foodiestudio:boring-plugins:0.2.1")
            }
        }
    }
}
rootProject.name = "libs-versions"