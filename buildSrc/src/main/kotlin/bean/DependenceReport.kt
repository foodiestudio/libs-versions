package bean

import kotlinx.serialization.Serializable

@Serializable
data class DependenceReport(
    val group: String,
    val name: String,
    val versions: MutableList<VersionReport>,
)

@Serializable
data class VersionReport(
    val version: String,
    val from: MutableList<String>,
)

@Serializable
data class MaybeCanUpgradeReport(
    val group: String,
    val name: String,
    val from: String,
    val version: String,
    val upgradeVersion: String,
)