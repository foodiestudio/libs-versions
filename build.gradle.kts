plugins {
    `version-catalog`
    `maven-publish`
// TODO
//    id("github-publish")
}

group = "com.github.foodiestudio"
version = "2023.01.00-rc.3"

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

// TODO
//github {
//    owner = "foodiestudio"
//    credentials {
//        username = localProp("github.username") ?: System.getenv("USERNAME")
//        password = localProp("github.classicToken") ?: System.getenv("TOKEN")
//    }
//    publishing {
//        repo("libs-versions") {
//            includeVersion = all()
//        }
//        repo("public") {
//            includeVersion = stableOnly()
//        }
//    }
//}