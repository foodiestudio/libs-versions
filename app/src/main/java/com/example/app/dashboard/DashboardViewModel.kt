package com.example.app.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.database.HockeyPlayer
import com.example.app.datasource.GithubRelease
import com.example.app.datasource.GithubService
import com.example.app.datasource.LocalDataSource
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStates
import com.google.android.play.core.ktx.packStates
import com.google.android.play.core.ktx.requestFetch
import com.google.android.play.core.ktx.requestPackStates
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.ktx.status
import kotlinx.coroutines.launch
import logcat.asLog
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

    private val assetPackName = "foo"

    suspend fun checkIsDownloaded(context: Context): AssetPackState? {
        val manager = AssetPackManagerFactory.getInstance(context)
        try {
            val assetPackStates: AssetPackStates =
                manager.requestPackStates(listOf(assetPackName))
            return assetPackStates.packStates()[assetPackName]
        } catch (e: RuntimeExecutionException) {
            logcat { e.asLog() }
            return null
        }
    }

    suspend fun requestDownload(context: Context) {
        val manager = AssetPackManagerFactory.getInstance(context)
        manager.requestFetch(listOf(assetPackName))
    }
}