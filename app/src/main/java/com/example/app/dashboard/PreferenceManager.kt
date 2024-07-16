package com.example.app.dashboard

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.foodiestudio.sugar.ExperimentalSugarApi
import com.github.foodiestudio.sugar.storage.AppFileHelper
import com.squareup.moshi.Moshi
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(context: Context) : CoroutineScope by MainScope() {

    private val moshi = Moshi.Builder().build()

    private val mmkv by lazy {
        MMKV.defaultMMKV()
    }

    @OptIn(ExperimentalSugarApi::class)
    private val appFileHelper = AppFileHelper(context)

    @OptIn(ExperimentalSugarApi::class)
    private val jsonFile: File by lazy {
        appFileHelper.requireFilesDir(false).resolve("Preference.json")
    }

    private val dataStore = context.dataStore

    fun save(key: String, value: String) {
        mmkv.encode(key, value)
        launch {
            dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
        val jsonAdapter = moshi.adapter(Map::class.java)
        val json = jsonAdapter.toJson(
            mapOf(
                key to value
            )
        )
        jsonFile.writeText(json)
    }

    fun loadByMMKV(key: String): String? {
        return mmkv.decodeString(key)
    }

    suspend fun loadByDatastore(key: String): String? {
        return dataStore.data.first()[stringPreferencesKey(key)]
    }


    // parse json
    fun loadByFile(key: String): String? {
        if (!jsonFile.exists()) return null

        val json: String = jsonFile.readText()
        val moshi = Moshi.Builder().build()
        val map = moshi.adapter(Map::class.java).fromJson(json) as Map<String, String>
        return map[key]
    }
}
