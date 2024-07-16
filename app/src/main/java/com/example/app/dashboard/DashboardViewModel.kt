package com.example.app.dashboard

import androidx.lifecycle.ViewModel
import com.example.app.database.HockeyPlayer
import com.example.app.datasource.GithubRelease
import com.example.app.datasource.GithubService
import com.example.app.datasource.LocalDataSource
import logcat.logcat

class DashboardViewModel(
    private val localDataSource: LocalDataSource,
    private val preferenceManager: PreferenceManager,
) : ViewModel() {

    private val githubService: GithubService = GithubService()

    private val tag = this.javaClass.simpleName

    suspend fun load(): List<HockeyPlayer> {
        logcat(tag) {
            """
                mmkv: ${preferenceManager.loadByMMKV("test")}
                data: ${preferenceManager.loadByDatastore("test")}
                file: ${preferenceManager.loadByFile("test")}                
            """.trimIndent()
        }
        return localDataSource.selectAll()
    }

    fun save(key: String, value: String) {
        preferenceManager.save(key, value)
    }

    suspend fun queryReleases(): List<GithubRelease> {
        return githubService.listReleases("kkoshin", "Muse")
    }
}