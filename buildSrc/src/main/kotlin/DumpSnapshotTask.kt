import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class DumpSnapshotTask : DefaultTask() {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @get:OutputDirectory
    abstract val snapshotFolder: DirectoryProperty

    @get:InputFile
    abstract val versionCatalog: RegularFileProperty

    @TaskAction
    fun run() {
        listOf(
            "implementationDependenciesMetadata",
            "releaseCompileClasspath",
            "releaseRuntimeClasspath"
        ).forEach {
            val implementationConfiguration = project.configurations.getByName(it)
            val resolvedDependencies =
                implementationConfiguration.incoming.resolutionResult.allDependencies
                    .filterIsInstance<ResolvedDependencyResult>()
            val snapshotFile = snapshotFolder.get().asFile.resolve("$it.json")
            snapshotFile.writeText(json.encodeToString(resolvedDependencies.run(DepAnalyticsManager::parse)))
            val canUpgradeReport = snapshotFile.resolveSibling("${it}_can_upgrade.json")
            canUpgradeReport.writeText(
                json.encodeToString(
                    resolvedDependencies.run(
                        DepAnalyticsManager::maybeCanUpgradeResult
                    )
                )
            )
        }
    }
}

