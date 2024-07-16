package com.example.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.dashboard.DashboardScreen
import com.example.app.photo.PhotoGrid
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun MainScreen(navController: NavHostController = rememberNavController()) {
    val routerContext = remember {
        RouterContextImpl(navController)
    }
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "/",
    ) {
        composable("/") {
            DashboardScreen(routerContext = routerContext)
        }

        bottomSheet("/photo") {
            PhotoGrid(Modifier.padding(top = 16.dp))
        }
    }
}
