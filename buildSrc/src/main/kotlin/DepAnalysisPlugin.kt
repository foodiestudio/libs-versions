import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class DepAnalysisPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register<DumpSnapshotTask>("snapshotDependencies") {
            group = "Dependency Analysis"
        }

        target.tasks.register<ValidateDepsTask>("validateDependencies") {
            group = "Dependency Analysis"
        }
    }
}