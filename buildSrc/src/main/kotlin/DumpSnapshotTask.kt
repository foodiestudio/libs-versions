import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class DumpSnapshotTask : DefaultTask() {

    @get:OutputFile
    abstract val snapshotFile: RegularFileProperty

    @TaskAction
    fun run() {
        val implementationConfiguration =
            project.configurations.getByName("implementationDependenciesMetadata")
        // 最终所采纳的依赖版本列表
        val dependencies = implementationConfiguration.incoming.resolutionResult.allDependencies
            .filterIsInstance<ResolvedDependencyResult>()
            .map { it.selected.moduleVersion.toString() }
            .toSortedSet()
            .joinToString("\n")

        snapshotFile.get().asFile.writeText(dependencies)
    }
}
