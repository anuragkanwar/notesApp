package com.example.todonotesapp.screens.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.todonotesapp.components.*
import com.example.todonotesapp.model.remote.UpdateProfile
import com.example.todonotesapp.navigation.NotesScreen
import com.example.todonotesapp.screens.OnEvent
import com.example.todonotesapp.screens.ResponseEvent
import com.example.todonotesapp.ui.theme.Manrope
import java.util.regex.Pattern

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Top
    ) {

        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            Box(modifier = Modifier.align(Alignment.TopStart)) {
                IconButton(
                    onClick = {
                        navController.navigate(NotesScreen.HomeScreen.name) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .padding(top = 18.dp, start = 18.dp)
                        .size(30.dp)
                        .background(MaterialTheme.colors.background)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back to Home Screen",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = {
                        userViewModel.logout()
                    },
                    modifier = Modifier
                        .padding(top = 18.dp, start = 18.dp)
                        .size(30.dp)
                        .background(MaterialTheme.colors.background)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }

            Box(modifier = Modifier.align(Alignment.Center)) {
                LottieComponent(
                    asset = "register.json",
                    speed = 1f,
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .align(Alignment.BottomEnd),
                    size = 700.dp
                )
            }
        }


        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Edit Profile",
                modifier = Modifier
                    .padding(top = 12.dp, start = 18.dp, bottom = 8.dp, end = 8.dp),
                color = MaterialTheme.colors.onBackground,
                fontFamily = Manrope,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            )
        }

        InputText(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            text = name,
            label = "Name",
            onTextChange = {
                if (it.all { char ->
                        char.isLetter() || char.isDigit()
                    }) {
                    name = it.trim()
                }
            }
        )

        InputText(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            text = email,
            label = "Email",
            onTextChange = {
                if (it.all { char ->
                        !char.isWhitespace()
                    }) {
                    email = it.trim()
                }
            }
        )

        MyButton(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 12.dp)
                .fillMaxWidth(),
            text = "Change!",
            onClick = {
                if (validateEmail(email)) {
                    userViewModel.editProfile(
                        UpdateProfile(
                            name = name,
                            email = email,
                            imageUrl = name
                        )
                    )
                } else {
                    Toast.makeText(context, "Check your fields...", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    val isLoading by remember {
        userViewModel.isLoading
    }

    OnEvent(userViewModel.event) {
        when (it) {
            ResponseEvent.Success -> {
                navController.navigate(NotesScreen.HomeScreen.name) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
            is ResponseEvent.Failure -> {
                Toast.makeText(context, "Some Error Occurred..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    }

}

private fun validateEmail(email: String): Boolean {
    val regex = "^[a-zA-Z0-9_&*-]+(?:\\.[a-zA-z0-9_&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    val pattern = Pattern.compile(regex)
    return (email.isNotEmpty() && pattern.matcher(email).matches())
}