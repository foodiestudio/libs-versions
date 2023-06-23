plugins {
    `version-catalog`
    id("github-publish")
}

group = "com.github.foodiestudio"
version = "2023.01.00-rc.6"

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

githubPackage {
    owner = "foodiestudio"
    credentials {
        username = rootProject.findLocalProp("github.username") ?: System.getenv("USERNAME")
        password = rootProject.findLocalProp("github.classicToken") ?: System.getenv("TOKEN")
    }
    packages {
        repo("libs-versions") {
            includeVersion = all()
        }
        repo("public") {
            includeVersion = stableOnly()
        }
    }
}