package com.example.app.datasource

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    @GET("repos/{owner}/{repo}/releases")
    suspend fun listReleases(@Path("owner") owner: String?, @Path("repo") repo: String?): List<GithubRelease>
}

@Serializable
data class GithubRelease(
    val url: String?,
    @SerialName("tag_name")
    val tagName: String?,
    val assets: List<GithubAsset>?
)

@Serializable
data class GithubAsset(
    val url: String?,
    val name: String?,
    @SerialName("download_count")
    val downloadCount: Int
)

fun GithubService(): GithubService {
    val contentType = "application/json".toMediaType()
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
        }.asConverterFactory(contentType))
        .build()
    return retrofit.create(GithubService::class.java)
}