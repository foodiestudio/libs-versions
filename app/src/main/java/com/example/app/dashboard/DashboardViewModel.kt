package com.example.app.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.database.HockeyPlayer
import com.example.app.datasource.GithubRelease
import com.example.app.datasource.GithubService
import com.example.app.datasource.LocalDataSource
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.play.core.assetpacks.AssetPackException
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.AssetPackStates
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.google.android.play.core.ktx.assetsPath
import com.google.android.play.core.ktx.packStates
import com.google.android.play.core.ktx.requestFetch
import com.google.android.play.core.ktx.requestPackStates
import com.google.android.play.core.ktx.requestProgressFlow
import com.google.android.play.core.ktx.status
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import java.io.File

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

    // onDemand 涉及到从 Google Play 下载资源，所以必须要先上传到 Google Play 上
    private val onDemandAssetPackName = "foo"

    // 其实用不到
    private val onInstallAssetPackName = "haha"

    fun loadAssetContent(context: Context): String {
        val assetManager = context.assets
        // 注意，这里并没有用到 onInstallAssetPackName，而是直接填写的 src/main/assets/ 下的相对路径
        return assetManager.open("hello/install_msg.txt").bufferedReader().use { it.readText() }
    }

    suspend fun checkIsDownloaded(context: Context): AssetPackState? {
        val manager = AssetPackManagerFactory.getInstance(context)
        try {
            val assetPackStates: AssetPackStates =
                manager.requestPackStates(listOf(onDemandAssetPackName))
            return assetPackStates.packStates()[onDemandAssetPackName]
        } catch (e: RuntimeExecutionException) {
            logcat { e.asLog() }
            return null
        }
    }

    suspend fun requestDownload(context: Context): Flow<Download> {
        val manager = AssetPackManagerFactory.getInstance(context)
        return manager.download(onDemandAssetPackName)
    }
}

sealed interface Download {
    class Progress(val percent: Int) : Download
    class Success(val assetFolder: File) : Download
    open class Error(val errorCode: Int) : Download
}

class DownloadUnknownError : Download.Error(-1001)

private const val CustomAssetPackErrorAssetNotFound = -1000

suspend fun AssetPackManager.download(assetPackName: String): Flow<Download> =
    callbackFlow {
        val listener =
            AssetPackStateUpdateListener { assetPackState ->
                when (assetPackState.status()) {
                    AssetPackStatus.DOWNLOADING -> {
                        val downloaded = assetPackState.bytesDownloaded()
                        val totalSize = assetPackState.totalBytesToDownload()
                        val percent = 100.0 * downloaded / totalSize
                        trySend(Download.Progress(percent.toInt()))
                    }

                    AssetPackStatus.COMPLETED -> {
                        val assetFolder = getPackLocation(assetPackName)?.assetsPath
                        if (assetFolder != null) {
                            trySend(Download.Success(File(assetFolder)))
                        } else {
                            trySend(
                                Download.Error(CustomAssetPackErrorAssetNotFound)
                            )
                        }
                        close()
                    }

                    AssetPackStatus.FAILED -> {
                        // Request failed. Notify user.
                        trySend(Download.Error(assetPackState.errorCode()))
                        close()
                    }

                    else -> {
                        logcat {
                            "AssetPackStatus: ${assetPackState.status()}"
                        }
                    }

//                    AssetPackStatus.CANCELED -> {
//                        // Request canceled. Notify user.
//                    }
//
//                    AssetPackStatus.WAITING_FOR_WIFI -> {
//                        Log.i(Log.Tag.Asr, "Wait for wifi")
//                    }
//
//                    AssetPackStatus.NOT_INSTALLED -> {
//                        // Asset pack is not downloaded yet.
//                    }
//
//                    AssetPackStatus.UNKNOWN -> {
//                        Log.w(Log.Tag.Asr, "Asset pack status unknown")
//                    }
                }
            }
        registerListener(listener)
        // requestFetch will fail if app is in background
        // do try catch explicitly for suspend function
        try {
            requestFetch(listOf(assetPackName))
        } catch (e: AssetPackException) {
            trySend(Download.Error(e.errorCode))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            trySend(DownloadUnknownError())
        }
        awaitClose {
            unregisterListener(listener)
        }
    }