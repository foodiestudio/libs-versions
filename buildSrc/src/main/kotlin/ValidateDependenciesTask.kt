import bean.DependenceReport
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

abstract class ValidateDependenciesTask : DefaultTask() {

    private val json = Json { ignoreUnknownKeys = true }

    @get:InputDirectory abstract val snapshotFolder: DirectoryProperty

    @TaskAction
    fun execute() {
        val implementationsFile =
                snapshotFolder.get().asFile.resolve("implementationDependenciesMetadata.json")
        if (!implementationsFile.exists()) {
            throw GradleException("implementationDependenciesMetadata.json not found")
        }
        val implementations =
                json.decodeFromString<List<DependenceReport>>(implementationsFile.readText())
        val expectKotlinVersion =
                implementations.filterVersion(GROUP_KOTLIN).requireOnlySingle(GROUP_KOTLIN)
        val definedKotlin =
                project.extensions
                        .getByType<VersionCatalogsExtension>()
                        .named("libs")
                        .findVersion("kotlin")
                        .get()
                        .requiredVersion

        if (expectKotlinVersion != definedKotlin) {
            throw GradleException("This task should only run without upgrading the Kotlin version.")
        }

        val expectComposeVersion =
                implementations.filterVersion(GROUP_COMPOSE).requireOnlySingle(GROUP_COMPOSE)
        val actualVersion = fetchCurrentVersion("implementationDependenciesMetadata")
        if (expectComposeVersion != actualVersion.compose) {
            throw GradleException(
                    "Expected compose version: $expectComposeVersion, actual compose version: ${actualVersion.compose}"
            )
        }
        logger.log(LogLevel.INFO, "Everything looks good.")
    }

    private fun fetchCurrentVersion(configurationName: String): ActualVersion {
        val result =
                project.configurations
                        .getByName(configurationName)
                        .incoming
                        .resolutionResult
                        .allDependencies
                        .filterIsInstance<ResolvedDependencyResult>()
                        .run(DepAnalyticsManager::parse)
        val kotlin = result.filterVersion(GROUP_KOTLIN).requireOnlySingle(GROUP_KOTLIN)
        val compose = result.filterVersion(GROUP_COMPOSE).requireOnlySingle(GROUP_COMPOSE)
        return ActualVersion(kotlin = kotlin, compose = compose)
    }

    private fun Set<String>.requireOnlySingle(groupName: String): String =
            this.let {
                if (it.size != 1) {
                    throw GradleException("Expected only one version of $groupName, but found $it")
                }
                it.first()
            }

    private fun List<DependenceReport>.filterVersion(groupName: String): Set<String> {
        return filter { it.group == groupName }
                .map {
                    check(it.versions.size == 1) {
                        "Expected only one version for ${it.group}:${it.name}"
                    }
                    it.versions.first().version
                }
                .toSet()
    }

    companion object {
        private const val GROUP_KOTLIN = "org.jetbrains.kotlin"
        private const val GROUP_COMPOSE = "androidx.compose.ui"
    }
}

data class ActualVersion(
        val kotlin: String,
        val compose: String,
)
