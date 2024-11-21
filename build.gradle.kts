plugins {
    `version-catalog`
    `maven-publish`
    signing
}

group = "io.github.foodiestudio"
version = "2023.10.00"

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
            pom {
                name.set("libs-versions")
                description.set("A version catalog")
                url.set("https://github.com/foodiestudio/libs-versions")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/foodiestudio/libs-versions/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("kkoshin")
                        name.set("Ko Shin")
                        email.set("koshin.1116@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/foodiestudio/libs-versions")
                    connection.set("scm:git:https://github.com/foodiestudio/libs-versions.git")
                    developerConnection.set("scm:git:https://github.com/foodiestudio/libs-versions.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"

            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"

            setUrl(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }

    signing {
        // gpg -K --keyid-format short
        val SIGNING_KEY_ID = System.getenv("SIGNING_KEY_ID")
        // gpg --armor --export-secret-key
        val SIGNING_KEY = System.getenv("SIGNING_KEY")
        val SIGNING_PASSWORD = System.getenv("SIGNING_PASSWORD")
        useInMemoryPgpKeys(SIGNING_KEY_ID, SIGNING_KEY, SIGNING_PASSWORD)
        sign(publishing.publications)
    }
}