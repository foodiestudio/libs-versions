import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class DepAnalysisPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register<DumpSnapshotTask>("snapshotDependencies") {
            group = GROUP
            snapshotFolder.set(target.layout.projectDirectory.dir("analytics"))
            versionCatalog.set(target.layout.projectDirectory.file("../gradle/libs.versions.toml"))
            description = "Dump snapshot dependencies to analytics folder"
        }

        val validateLint =
            target.tasks.register("validateAgpLintVersion") {
                doLast {
                    val libs =
                        project.extensions.getByType<VersionCatalogsExtension>().named("libs")
                    val agp = libs.findVersion("agp").get().requiredVersion
                    val lint = libs.findVersion("lint").get().requiredVersion
                    lint.split('.').map { it.toInt() }
                        .let { (major, minor, patch) ->
                            agp.split('.').map { it.toInt() }
                                .let { (major2, minor2, patch2) ->
                                    if (!(major == major2 + 23 && minor == minor2 && patch == patch2)) {
                                        val fixedLintVersion = "${major2 + 23}.${minor2}.${patch2}"
                                        throw GradleException("Lint version $lint is not compatible with AGP version $agp, please update lint version to $fixedLintVersion")
                                    }
                                }
                        }
                }
            }

        val validateKsp = target.tasks.register("validateKspVersion") {
            doLast {
                val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
                val kotlin = libs.findVersion("kotlin").get().requiredVersion
                val ksp = libs.findVersion("ksp").get().requiredVersion
                if (!ksp.startsWith(kotlin)) {
                    throw GradleException("KSP version ($ksp) should use the same as Kotlin version ($kotlin)")
                }
            }
        }

        target.tasks.register<ValidateDependenciesTask>("validateDependencies") {
            group = GROUP
            dependsOn(validateLint, validateKsp)
            snapshotFolder.set(target.layout.projectDirectory.dir("analytics"))
            versionCatalog.set(target.layout.projectDirectory.file("../gradle/libs.versions.toml"))
            description = "Validate dependencies if you are not making a breaking change"
        }
    }

    companion object {
        private const val GROUP = "Dependency analytics"
    }
}