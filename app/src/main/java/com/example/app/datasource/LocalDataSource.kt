package com.example.app.datasource

import android.content.Context
import com.example.app.database.AppDatabase
import com.example.app.database.HockeyPlayer
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(context: Context) {

    private val driver: SqlDriver = AndroidSqliteDriver(AppDatabase.Schema, context, "test.db")
    private val playerQueries = AppDatabase(driver).playerQueries

    suspend fun selectAll(): List<HockeyPlayer> = withContext(Dispatchers.IO) {
        playerQueries.selectAll().executeAsList()
    }

    fun insert(playerNumber: Long, fullName: String) = playerQueries.insert(playerNumber, fullName)
}

