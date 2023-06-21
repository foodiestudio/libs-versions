plugins {
    `version-catalog`
    `maven-publish`
}

group = "com.github.foodiestudio"
version = "2023.01.00-rc.2"

catalog {
    // declare the aliases, bundles and versions in this block
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])
        }
    }
}

// fixing Task 'assemble' not found in root project
tasks.register("assemble") {
    // do nothing, just make jitpack happy
}