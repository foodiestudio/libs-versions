package com.example.app

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.example.app.dashboard.DashboardViewModel
import com.example.app.dashboard.PreferenceManager
import com.example.app.datasource.LocalDataSource
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.system.measureTimeMillis

class AppInitializer : Initializer<Unit> {

    private val tag = this.javaClass.simpleName

    override fun create(context: Context) {
        measureTimeMillis {
            runBlocking {
                initLogcat(context as Application)
                launch {
                    initPreference(context)
                }
                launch {
                    initKoin(context)
                }
            }
        }.let {
            logcat(tag) {
                "AppInitializer finished: $it ms"
            }
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}

private fun initLogcat(application: Application) {
    AndroidLogcatLogger.installOnDebuggableApp(application, minPriority = LogPriority.VERBOSE)
}

private fun initPreference(context: Context) {
    MMKV.initialize(context)
}

private val appModule = module {
    singleOf(::LocalDataSource)
    factory { PreferenceManager(get()) }
    viewModelOf(::DashboardViewModel)
}

private fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(appModule)
    }
}