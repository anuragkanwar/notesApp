package com.example.todonotesapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todonotesapp.screens.*
import com.example.todonotesapp.screens.user.LoginScreen
import com.example.todonotesapp.screens.user.UserViewModel
import com.example.todonotesapp.screens.addEditNote.AddEditNoteScreen
import com.example.todonotesapp.screens.addEditNote.AddEditNoteViewModel
import com.example.todonotesapp.screens.addEditNote.LockedScreen
import com.example.todonotesapp.screens.home.HomeScreen
import com.example.todonotesapp.screens.home.HomeViewModel
import com.example.todonotesapp.screens.user.EditProfileScreen
import com.example.todonotesapp.screens.user.UserScreens

@Composable
fun NotesNavigation(
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NotesScreen.SplashScreen.name
    ) {
        composable(NotesScreen.SplashScreen.name) {
            SplashScreen(navController = navController)
        }

        composable(
//            NotesScreen.HomeScreen.name + "/{imageUrl}",
//            arguments = listOf(navArgument(name = "imageUrl"){type = NavType.StringType})
            NotesScreen.HomeScreen.name
        ) { backstackEntry ->
            val homeViewModel = hiltViewModel<HomeViewModel>()
//            HomeScreen(navController = navController,homeViewModel,backstackEntry.arguments?.getString("imageUrl"))
            HomeScreen(navController = navController, homeViewModel)
        }

        composable(
            NotesScreen.AddEditNoteScreen.name + "?noteId={noteId}&noteColor={noteColor}",
            arguments = listOf(
                navArgument(name = "noteId"){
                    type = NavType.StringType
                    defaultValue = "-1"
                },
                navArgument(name = "noteColor"){
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ){
            val color = it.arguments?.getInt("noteColor") ?: -1
            val addEditNoteViewModel = hiltViewModel<AddEditNoteViewModel>()
            AddEditNoteScreen(navController = navController, noteColor = color)
        }

        composable(
            NotesScreen.LockedScreenFile.name + "?noteId={noteId}&noteColor={noteColor}",
            arguments = listOf(
                navArgument(name = "noteId"){
                    type = NavType.StringType
                    defaultValue = "-1"
                },
                navArgument(name = "noteColor"){
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ){
            val color = it.arguments?.getInt("noteColor") ?: -1
            val id = it.arguments?.getString("noteId") ?: "-1"
            val addEditNoteViewModel = hiltViewModel<AddEditNoteViewModel>()
            LockedScreen(navController = navController,addEditNoteViewModel, noteColor = color,noteId = id)
        }

        composable(NotesScreen.LoginScreen.name) {
            val userViewModel = hiltViewModel<UserViewModel>()
            LoginScreen(navController = navController, userViewModel)
        }

        composable(NotesScreen.CreateUserScreen.name) {
            val userViewModel = hiltViewModel<UserViewModel>()
            CreateUserScreen(navController = navController, userViewModel)
        }

        composable(NotesScreen.EditProfileScreen.name) {
            val userViewModel = hiltViewModel<UserViewModel>()
            EditProfileScreen(navController = navController, userViewModel)
        }

        composable(NotesScreen.UserScreen.name) {
            val userViewModel = hiltViewModel<UserViewModel>()
            UserScreens(navController = navController, userViewModel)
        }
    }

}