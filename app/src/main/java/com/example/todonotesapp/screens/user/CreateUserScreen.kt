package com.example.todonotesapp.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.todonotesapp.components.*
import com.example.todonotesapp.model.remote.RegisterUser
import com.example.todonotesapp.navigation.NotesScreen
import com.example.todonotesapp.screens.user.UserViewModel
import com.example.todonotesapp.ui.theme.Manrope
import java.util.regex.Pattern

@SuppressLint("RememberReturnType")
@Composable
fun CreateUserScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {

    var name by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {

        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(300.dp)
        ) {
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

            LottieComponent(
                asset = "register.json",
                speed = 1f,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .align(Alignment.BottomEnd),
                size = 700.dp
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign Up",
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
                    name = it
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

        InputPasswordText(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth(),
            text = password,
            label = "Password",
            onTextChange = {
                if (it.all { char ->
                        !char.isWhitespace()
                    }
                ) {
                    password = it.trim()
                }
            }
        )

        Text(
            text = "1. Password length should be in between 8 to 20." +
                    "\n2. Password should contain at least one lowercase character." +
                    "\n3. At least one upper case\n4. At least one digit" +
                    "\n5. At least one special character ",
            color = Color.LightGray,
            fontFamily = Manrope,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        MyButton(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 12.dp)
                .fillMaxWidth(),
            text = "Sign up",
            onClick = {
                if (name.isNotEmpty() and validateEmail(email) and validatePassword(password)) {
                    userViewModel.createUser(
                        RegisterUser(
                            email = email,
                            name = name,
                            password = password,
                            imageUrl = name
                        )
                    )
                } else {
                    Toast.makeText(context, "Check your fields...", Toast.LENGTH_SHORT).show()
                }
            }
        )


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp,
                        fontFamily = Manrope
                    )
                ) {
                    append("Already have an account?")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 20.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Log in!")
                }
            },
                modifier = Modifier.clickable {
                    navController.navigate(NotesScreen.LoginScreen.name) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                }
            )
        }
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

    if(isLoading){
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
    var regex = "^[a-zA-Z0-9_&*-]+(?:\\.[a-zA-z0-9_&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    val pattern = Pattern.compile(regex)
    return (email.isNotEmpty() && pattern.matcher(email).matches())
}

private fun validatePassword(password: String): Boolean {
    val regex = "(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,20}"
    val pattern = Pattern.compile(regex)
    return (password.isNotEmpty() && pattern.matcher(password).matches())

}