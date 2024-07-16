package com.example.app

import androidx.navigation.NavController

interface RouterContext {
    fun launchPhotoGrid()
}

class RouterContextImpl(private val navController: NavController) : RouterContext {
    override fun launchPhotoGrid() = navController.navigate("/photo")
}