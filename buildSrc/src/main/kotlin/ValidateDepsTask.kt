import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.getByType

/**
 * 根据现有的校验规则来检查依赖版本的合理性
 */
abstract class ValidateDepsTask : DefaultTask() {

//    @get:Option(option = "kotlin", description = "target Kotlin version")
//    @get:Input
//    abstract val kotlinVersion: Property<String>

//    @get:Input
//    abstract val composeVersion: Property<String>
//
    @get:InputFile
    abstract val baselineFile: RegularFileProperty

    /**
     * check current version is match with old version
     * such as Compose, Kotlin, etc
     */
    @TaskAction
    fun check() {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val agp = libs.findVersion("agp").get().requiredVersion
        val lint = libs.findVersion("lint").get().requiredVersion
        checkLintCompatWithAGP(agp = agp, lint = lint)
        // TODO: 检查 compose 等版本是否满足期望
        val currentVersion = TODO("读取现在依赖里的版本")
        val expectVersion = TODO()
    }

    // 历史原因：lint 版本号在 AGP 版本号上加了 23 的值
    private fun checkLintCompatWithAGP(agp: String, lint: String) {
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