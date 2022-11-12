package com.example.todonotesapp.screens.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.todonotesapp.components.LoadingIndicator

@Composable
fun UserScreens(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    userViewModel.getCurrentUser()

    val isLoading by remember {
        userViewModel.isLoading
    }

    val success by remember {
        userViewModel.isSuccess
    }

    val loadError by remember {
        userViewModel.loadError
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            LoadingIndicator()
        }
        if (loadError.isNotEmpty()) {
            LoginScreen(navController = navController, userViewModel = userViewModel)
        }
        AnimatedVisibility(visible = success) {
            EditProfileScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}