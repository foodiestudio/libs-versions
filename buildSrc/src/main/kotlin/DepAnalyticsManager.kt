import bean.DependenceReport
import bean.MaybeCanUpgradeReport
import bean.VersionReport
import org.gradle.api.artifacts.result.ResolvedDependencyResult

object DepAnalyticsManager {

    fun parse(result: List<ResolvedDependencyResult>): List<DependenceReport> {
        val report = mutableListOf<DependenceReport>()
        result.forEach {
            with(it.selected.moduleVersion!!) {
                report.find { it.group == group && it.name == name }?.versions?.run {
                    find { it.version == version }?.from?.add(it.from.toString())
                        ?: add(VersionReport(version, mutableListOf(it.from.toString())))
                }
                    ?: report.add(
                        DependenceReport(
                            group,
                            name,
                            mutableListOf(
                                VersionReport(
                                    version,
                                    mutableListOf(it.from.toString())
                                )
                            )
                        )
                    )
            }
        }
        return report
    }

    fun maybeCanUpgradeResult(result: List<ResolvedDependencyResult>): List<MaybeCanUpgradeReport> {
        return result
            .filter { it.selected.moduleVersion.toString() != it.requested.displayName }
            .map {
                with(it.selected.moduleVersion!!) {
                    MaybeCanUpgradeReport(
                        group = group,
                        name = name,
                        from = it.from.toString(),
                        version = it.requested
                            .displayName
                            .split(":")
                            .last(),
                        upgradeVersion = it.selected.moduleVersion!!.version
                    )
                }
            }
            .distinctBy { it.from }
    }
}
