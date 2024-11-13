import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DepAnalysisPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val dumpTaskProvider = target.tasks.register<DumpSnapshotTask>("snapshotDependencies") {
            group = "Dependency Analysis"
            snapshotFile.convention(target.layout.projectDirectory.file("analysis/snapshot.txt"))
        }

        target.tasks.register<ValidateDepsTask>("validateDependencies") {
            group = "Dependency Analysis"
            baselineFile.set(dumpTaskProvider.flatMap { it.snapshotFile })
        }
    }
}