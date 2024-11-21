package com.example.app.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.app.RouterContext
import com.example.app.database.HockeyPlayer
import com.example.app.datasource.GithubRelease
import com.github.foodiestudio.sugar.notification.toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
    routerContext: RouterContext
) {
    var list: List<HockeyPlayer> by remember {
        mutableStateOf(emptyList())
    }

    var release: List<GithubRelease> by remember {
        mutableStateOf(emptyList())
    }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var statusCode by remember {
        mutableStateOf(-1)
    }


    LaunchedEffect(Unit) {
        list = viewModel.load()
        runCatching {
            release = viewModel.queryReleases()
        }.onFailure { err ->
            err.printStackTrace()
            withContext(Dispatchers.Main) {
                context.toast(err.message)
            }
        }
    }

    LazyColumn(modifier) {
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {
                    routerContext.launchPhotoGrid()
                }) {
                    Text("Pick photo")
                }
                Button(onClick = {
                    viewModel.save("test", Random.nextInt().toString())
                }) {
                    Text("Save test")
                }
            }
        }
        items(list) {
            Text(
                it.full_name,
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        items(release) {
            Column {
                Text(it.tagName ?: "", style = MaterialTheme.typography.h3)
                it.assets?.forEach { item ->
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(item.name ?: "")
                        Text("Download count: ${item.downloadCount}")
                    }
                }
            }
        }

        item {
            Button(onClick = {
                scope.launch {
                    runCatching {
                        viewModel.checkIsDownloaded(context)?.let {
                            statusCode = it.status()
                        }
                    }.onFailure {
                        if (it is CancellationException) {
                            throw it
                        }
                        logcat { it.asLog() }
                    }
                }
            }) {
                Text("check asset status: $statusCode")
            }
        }

        item {
            Button(onClick = {
                runCatching {
                    viewModel.loadAssetContent(context)
                }.onSuccess {
                    context.toast(it)
                }.onFailure {
                    if (it is CancellationException) {
                        throw it
                    }
                    logcat { it.asLog() }
                }
            }) {
                Text("read install asset")
            }
        }
    }
}