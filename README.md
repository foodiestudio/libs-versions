## version catalog
provide common version catalog

### Usage
[![](https://jitpack.io/v/foodiestudio/libs-versions.svg)](https://jitpack.io/#foodiestudio/libs-versions)

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        //...
        maven { url 'https://jitpack.io' }
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