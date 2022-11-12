package com.example.todonotesapp.screens.addEditNote

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.todonotesapp.R
import com.example.todonotesapp.components.CompleteDialogContent
import com.example.todonotesapp.components.LoadingIndicator
import com.example.todonotesapp.components.TransparentHintTextField
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.navigation.NotesScreen
import com.example.todonotesapp.screens.notes.UiEvent
import com.example.todonotesapp.ui.theme.Manrope
import com.example.todonotesapp.utils.AppColors
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditNoteScreen(
    navController: NavHostController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {

    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value

    val checkPointState = viewModel.checkpoints


    val scaffoldState = rememberScaffoldState()

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )
    }

    val scope = rememberCoroutineScope()

    val dialogState: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Loading -> {
                    isLoading.value = true
                }
                is UiEvent.ShowSnackbar -> {
                    isLoading.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message + "Try again Later"
                    )
                }
                is UiEvent.SaveNote -> {
                    isLoading.value = false
                    navController.navigate(NotesScreen.HomeScreen.name) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                }
                is UiEvent.LockedNote -> {
                    isLoading.value = false
                }
            }
        }
    }


    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)

    val ctx: Context = LocalContext.current

    BackHandler() {
        if (titleState.text
                .trim()
                .isNotEmpty() && (contentState.text
                .trim()
                .isNotEmpty() || checkPointState.isNotEmpty())
        ) {
            viewModel.onEvent(AddEditNoteEvent.SaveNote)
        } else {
            navController.navigate(NotesScreen.HomeScreen.name){
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    actionColor = Color.White,
                    snackbarData = data,
                    backgroundColor = AppColors.blackonbg,
                    contentColor = Color.White
                )
            }
        }
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                BottomSheetContent(
                    scope,
                    viewModel,
                    noteBackgroundAnimatable
                )
            },
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetBackgroundColor = AppColors.blackonbg,
            sheetElevation = 5.dp,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(noteBackgroundAnimatable.value)
                    .padding(it)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .clickable {
                                navController.navigate(NotesScreen.HomeScreen.name) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                }
                            }
                            .size(30.dp),
                        tint = AppColors.white
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 7.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.icons8_paper),
                            contentDescription = "Bottom Sheet",
                            tint = AppColors.white,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable() {
                                    scope.launch {
                                        if (sheetState.isVisible) {
                                            sheetState.animateTo(
                                                ModalBottomSheetValue.Hidden,
                                                tween(500)
                                            )
                                        } else {
                                            sheetState.animateTo(
                                                ModalBottomSheetValue.Expanded,
                                                tween(500)
                                            )
                                        }
                                    }
                                }
                                .size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(13.dp))


                        Icon(
                            painter = painterResource(id = R.drawable.icons8_add_property),
                            contentDescription = "Add Button",
                            modifier = Modifier
                                .clickable {
                                    dialogState.value = true
                                }
                                .size(28.dp),
                            tint = AppColors.white
                        )

                        Spacer(modifier = Modifier.width(13.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.icons8_checkmark_50),
                            contentDescription = "Done Button",
                            modifier = Modifier
                                .clickable {
                                    if (titleState.text
                                            .trim()
                                            .isNotEmpty() && (contentState.text
                                            .trim()
                                            .isNotEmpty() || checkPointState.isNotEmpty())
                                    ) {
                                        viewModel.onEvent(AddEditNoteEvent.SaveNote)
                                    } else {
                                        Toast
                                            .makeText(ctx, "Empty Fields", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                                .size(30.dp),
                            tint = AppColors.white
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    TransparentHintTextField(
                        text = titleState.text,
                        hint = titleState.hintText,
                        onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                        onFocusChange = { viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it)) },
                        isHintVisible = titleState.isHintVisible,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = AppColors.white
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = viewModel.noteLabel.value,
                            fontFamily = Manrope,
                            fontSize = 19.sp,
                            color = Color(
                                ColorUtils.blendARGB(
                                    viewModel.noteColor.value,
                                    0x000000,
                                    0.5f
                                )
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(8.dp)
                    ) {

                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(3.dp)
                                .background(
                                    color = Color(
                                        ColorUtils.blendARGB(
                                            viewModel.noteColor.value,
                                            0x000000,
                                            0.5f
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TransparentHintTextField(
                            text = contentState.text,
                            hint = contentState.hintText,
                            onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                            onFocusChange = {
                                viewModel.onEvent(
                                    AddEditNoteEvent.ChangeContentFocus(
                                        it
                                    )
                                )
                            },
                            isHintVisible = contentState.isHintVisible,
                            singleLine = false,
                            textStyle = TextStyle(
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 22.sp,
                                color = AppColors.white
                            ),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    checkPointState.forEach {
                        Checkpoints(
                            ckpt = it,
                            color = AppColors.white
                        ) {
                            it.checked = !it.checked
                            viewModel.onEvent(AddEditNoteEvent.ChangeCheckpoint(it))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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


    // Dialog state Manager

    if (dialogState.value) {
        Dialog(
            onDismissRequest = { dialogState.value = false },
            content = {
                CompleteDialogContent(
                    title = "Todo Item",
                    dialogState = dialogState,
                    successButtonText = "DONE"
                ) {
                    viewModel.onEvent(
                        AddEditNoteEvent.AddCheckPoint(
                            NoteCheckpoints(
                                checked = false,
                                content = it
                            )
                        )
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetContent(
    scope: CoroutineScope,
    viewModel: AddEditNoteViewModel,
    noteBackgroundAnimatable: Animatable<Color, AnimationVector4D>
) {

    val dialogState: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LocalNote.noteColors.forEach { color ->
                val colorInt = color.toArgb()
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .shadow(15.dp, CircleShape)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 3.dp,
                            color = if (viewModel.noteColor.value == colorInt) {
                                Color(
                                    ColorUtils.blendARGB(
                                        viewModel.noteColor.value,
                                        0x000000,
                                        0.2f
                                    )
                                )
                            } else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            scope.launch {
                                noteBackgroundAnimatable.animateTo(
                                    targetValue = Color(colorInt),
                                    animationSpec = tween(
                                        durationMillis = 500
                                    )
                                )
                            }
                            viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.background(Color.Transparent)) {
                        this@Row.AnimatedVisibility(visible = (viewModel.noteColor.value == colorInt)) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Selected",
                                tint = AppColors.blackbg
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        val context = LocalContext.current


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(enabled = viewModel.currentNoteId != null) {
                    var checkpoints = ""

                    viewModel.checkpoints.forEachIndexed { index, noteCheckpoints ->
                        checkpoints += "$index). "
                        checkpoints += (noteCheckpoints.content)
                        if (noteCheckpoints.checked) {
                            checkpoints += " (DONE) "
                        }
                        checkpoints += "\n"
                    }

                    val note =
                        "Title : ${viewModel.noteTitle.value.text.trim()}\nContent: ${viewModel.noteContent.value.text.trim()}\nTodos:\n${checkpoints}\nLabel : ${viewModel.noteLabel.value}"

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, note)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.icons8_connect_50),
                contentDescription = "Delete Note",
                modifier = Modifier.size(24.dp),
                tint = if (viewModel.currentNoteId != null) AppColors.white else AppColors.grayText
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Share note",
                fontSize = 23.sp,
                fontFamily = Manrope,
                color = if (viewModel.currentNoteId != null) AppColors.white else AppColors.grayText
            )
        }

        Spacer(modifier = Modifier.height(10.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(enabled = viewModel.noteConnected.value) {
                    if (viewModel.noteLocked.value) {
                        viewModel.onEvent(AddEditNoteEvent.SetUnsetLock(null))
                    } else {
                        dialogState.value = true
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = if (!viewModel.noteLocked.value) "Lock Note" else "Unlock",
                modifier = Modifier.size(24.dp),
                tint = if (viewModel.noteConnected.value) AppColors.white else AppColors.grayText
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (!viewModel.noteLocked.value) "Lock Note" else "Unlock note",
                fontSize = 23.sp,
                fontFamily = Manrope,
                color = if (viewModel.noteConnected.value) AppColors.white else AppColors.grayText
            )

        }

        Spacer(modifier = Modifier.height(10.dp))


        var expandedState by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Label,
                        contentDescription = "Label",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Labels",
                        fontSize = 23.sp,
                        fontFamily = Manrope,
                        color = AppColors.white
                    )
                }

                Icon(
                    imageVector = if (expandedState) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Up/Down",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            expandedState = !expandedState
                        },
                    tint = AppColors.grayLightText
                )
            }

            AnimatedVisibility(visible = expandedState) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisAlignment = MainAxisAlignment.Start,
                    mainAxisSize = SizeMode.Expand,
                    crossAxisSpacing = 8.dp,
                    mainAxisSpacing = 8.dp
                ) {
                    LocalNote.noteLabels.forEach {
                        val label = it
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.Transparent)
                                .border(
                                    width = if (viewModel.noteLabel.value == label) {
                                        1.dp
                                    } else 0.5.dp,
                                    color = if (viewModel.noteLabel.value == label) {
                                        AppColors.white
                                    } else AppColors.grayText
                                )
                                .clickable {
                                    viewModel.onEvent(AddEditNoteEvent.ChangeLabel(label))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (viewModel.noteLabel.value == label) {
                                    AppColors.white
                                } else AppColors.grayText,
                                fontFamily = Manrope,
                                fontSize = 20.sp,
                                fontWeight = if (viewModel.noteLabel.value == label) {
                                    FontWeight.Bold
                                } else FontWeight.Normal,
                                maxLines = 5,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                    }
                }

            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }





    if (dialogState.value) {
        Dialog(
            onDismissRequest = { dialogState.value = false },
            content = {
                CompleteDialogContent(
                    title = "Your Safe Password",
                    isPassword = true,
                    hintText = "Enter your Safe Password",
                    dialogState = dialogState,
                    successButtonText = "DONE"
                ) {
                    viewModel.onEvent(AddEditNoteEvent.SetUnsetLock(it))
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}


@Composable
fun Checkpoints(
    modifier: Modifier = Modifier,
    ckpt: NoteCheckpoints,
    color: Color = AppColors.blackbg,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (ckpt.checked) painterResource(id = R.drawable.ic_icons8_filled_checkmark) else painterResource(
                id = R.drawable.icons8_circled_thin_50
            ),
            contentDescription = "Checkpoint",
            tint = color,
            modifier = Modifier.size(23.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = ckpt.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDecoration = if (ckpt.checked) (TextDecoration.LineThrough) else TextDecoration.None),
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            color = color
        )
    }
}



