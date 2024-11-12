import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class DumpSnapshotTask : DefaultTask() {

    @get:OutputFile
    abstract val snapshotFile: RegularFileProperty

    @TaskAction
    fun run() {
        // todo run :app:dependencies --configuration implementationDependenciesMetadata
        // TODO: 将当前版本信息提取出来，并且持久化下来
    }
}