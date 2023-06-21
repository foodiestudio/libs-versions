## version catalog
provide common version catalog

### Usage
[![](https://jitpack.io/v/foodiestudio/libs-versions.svg)](https://jitpack.io/#foodiestudio/libs-versions)

```groovy
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //...
        maven {
            name = "Github Packages"
            url = uri("https://maven.pkg.github.com/foodiestudio/libs-versions")
            credentials {
                // your github username
                username = extra["github.username"] as String
                // https://github.com/settings/tokens
                password = extra["github.token"] as String
            }
        }
    }
    versionCatalogs {
        create("sharedLibs") {
            from("com.github.foodiestudio:libs-versions:$version")
        }
    }
}
```

```groovy
// build.gradle (module)
dependencies {
    // We can reference it in
    implementation sharedLibs.ctc
}
```