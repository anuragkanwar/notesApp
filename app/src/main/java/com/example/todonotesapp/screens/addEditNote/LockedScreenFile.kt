package com.example.todonotesapp.screens.addEditNote

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.todonotesapp.components.CompleteDialogContent
import com.example.todonotesapp.components.InputPasswordText
import com.example.todonotesapp.components.LoadingIndicator
import com.example.todonotesapp.components.TransparentHintTextField
import com.example.todonotesapp.model.remote.SafePassword
import com.example.todonotesapp.navigation.NotesScreen
import com.example.todonotesapp.screens.notes.UiEvent
import com.example.todonotesapp.ui.theme.Manrope
import com.example.todonotesapp.utils.AppColors
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LockedScreen(
    navController: NavHostController,
    viewModel: AddEditNoteViewModel,
    noteColor: Int,
    noteId: String
) {


    val context = LocalContext.current

    val isLoading = remember {
        mutableStateOf(false)
    }
    val passWordDialogState = remember {
        mutableStateOf(true)
    }



    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Loading -> {
                    isLoading.value = true
                }
                is UiEvent.ShowSnackbar -> {
                    isLoading.value = false
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
//                    navController.navigateUp()
                }
                is UiEvent.SaveNote -> {
                    isLoading.value = false
//                    navController.navigateUp()
                }
                is UiEvent.LockedNote -> {
                    isLoading.value = false
                    Log.d("TAG","LOCKED SCREEN : CAN ACCESS")
                    navController.navigate(
                        NotesScreen.AddEditNoteScreen.name + "?noteId=${noteId}&noteColor=${noteColor}"
                    )
//                    passWordDialogState.value = false
                }
            }
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
    }


    if (passWordDialogState.value) {
        Dialog(
            onDismissRequest = { passWordDialogState.value = false },
            content = {
                LockedCompleteDialogContent(
                    title = "My Safe Password",
                    isPassword = true,
                    hintText = "Enter your Safe Password",
                    dialogState = passWordDialogState,
                    successButtonText = "Unlock!",
                ) {
                    viewModel.onEvent(
                        AddEditNoteEvent.LockedNote(
                            noteId = noteId,
                            SafePassword(it.trim())
                        )
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }else{
        navController.navigateUp()
    }

}

@Composable
fun LockedCompleteDialogContent(
    title: String,
    isPassword: Boolean = false,
    hintText: String = "Enter Checkpoint...",
    dialogState: MutableState<Boolean>,
    successButtonText: String,
    onAdd: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(0.6f),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = AppColors.blackonbg
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TitleAndButton(title, dialogState)
            Spacer(modifier = Modifier.height(8.dp))
            AddBody(
                successButtonText,
                isPassword = isPassword,
                dialogState = dialogState,
                hintText = hintText,
            ) {
                onAdd(it)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
private fun TitleAndButton(
    title: String,
    dialogState: MutableState<Boolean>,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                color = AppColors.grayLightText
            )
            IconButton(modifier = Modifier.then(Modifier.size(24.dp)),
                onClick = {
                    dialogState.value = false
                }) {
                Icon(
                    Icons.Filled.Close,
                    "contentDescription",
                    tint = AppColors.grayLightText
                )
            }
        }
        Divider(color = Color.DarkGray, thickness = 1.dp)
    }
}




@Composable
private fun AddBody(
    successButtonText: String,
    isPassword: Boolean,
    hintText: String,
    dialogState: MutableState<Boolean>,
    onAdd: (String) -> Unit
) {

    val text = remember {
        mutableStateOf("")
    }

    val isHintDisplayed = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .border(1.dp, shape = RoundedCornerShape(5.dp), color = AppColors.grayLightText)
    ) {
        if (isPassword) {
            InputPasswordText(
                text = text.value,
                label = hintText,
                onTextChange = { text.value = it },
                maxLine = 1,
                modifier = Modifier.padding(18.dp)
            )
        } else {
            TransparentHintTextField(
                text = text.value,
                hint = hintText,
                onValueChange = { text.value = it },
                onFocusChange = {
                    isHintDisplayed.value = !it.isFocused && text.value.isBlank()
                },
                isHintVisible = isHintDisplayed.value,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Normal,
                    fontSize = 22.sp,
                    color = AppColors.white,
                ),
                modifier = Modifier.padding(18.dp)
            )
        }

    }

    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                dialogState.value = false
            },
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 5.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Cancel", fontSize = 20.sp)
        }
        Button(
            onClick = {
                onAdd(text.value)
//                dialogState.value = false
            },
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = successButtonText, fontSize = 20.sp)
        }

    }
}