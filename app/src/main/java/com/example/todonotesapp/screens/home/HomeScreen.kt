package com.example.todonotesapp.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size
import com.example.todonotesapp.components.NoteItem
import com.example.todonotesapp.navigation.NotesScreen
import com.example.todonotesapp.screens.notes.NotesEvent
import com.example.todonotesapp.screens.notes.UiEvent
import com.example.todonotesapp.ui.theme.Manrope
import com.example.todonotesapp.utils.AppColors
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {

    val isLoading = remember {
        mutableStateOf(false)
    }


    val scaffoldState = rememberScaffoldState()



    LaunchedEffect(Unit) {
        homeViewModel.getImageIcon()
    }


    LaunchedEffect(key1 = true) {
        homeViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Loading -> {
                    isLoading.value = true
                }
                is UiEvent.ShowSnackbar -> {
                    isLoading.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
                is UiEvent.SaveNote -> {
                    isLoading.value = false
                }
                is UiEvent.LockedNote -> {
                    isLoading.value = false
                }
            }
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NotesScreen.AddEditNoteScreen.name)
                },
                backgroundColor = AppColors.blue,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add note",
                    modifier = Modifier.size(35.dp),
                    tint = AppColors.white
                )
            }
        },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(6.dp)
                .background(MaterialTheme.colors.background)
        ) {
            Header(
                homeViewModel = homeViewModel
            ) {
                navController.navigate(NotesScreen.UserScreen.name) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }

            SearchBar(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            ) { query ->
                if(query.isEmpty()){
                    homeViewModel.onEvent(NotesEvent.OrderNotes)
                }
                else{
                    homeViewModel.onEvent(NotesEvent.SearchNote(query))
                }
            }

            StaggeredGridView(
                navController = navController,
                homeViewModel = homeViewModel,
                scope = scope,
                scaffoldState = scaffoldState
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StaggeredGridView(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {


    val state = homeViewModel.state.value


    var isRefreshing by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isRefreshing){
        if(isRefreshing){
            delay(3000)
            isRefreshing = false
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            isRefreshing = true
            homeViewModel.syncNotes(){
                isRefreshing = false
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                CustomStaggeredVerticalGrid(
                    numColumns = 2,
                    modifier = Modifier.padding(5.dp)
                ) {
                    state.notes.forEach { note ->
                        key(note.noteId){
                            val dismissState = rememberDismissState()
                            if (dismissState.isDismissed(DismissDirection.EndToStart) || dismissState.isDismissed(
                                    DismissDirection.StartToEnd
                                )
                            ) {
                                homeViewModel.onEvent(NotesEvent.DeleteNote(note))
                                scope.launch {
                                    val result = scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Note Deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        homeViewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }


                            SwipeToDismiss(
                                state = dismissState,
                                modifier = Modifier.padding(vertical = 1.dp),
                                directions = setOf(
                                    DismissDirection.EndToStart,
                                    DismissDirection.StartToEnd
                                ),
                                dismissThresholds = { direction ->
                                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                                },
                                background = {
                                    Box(modifier = Modifier.background(Color.Transparent))
                                }
                            ) {
                                NoteItem(
                                    note = note,
                                    modifier = Modifier.padding(100.dp).background(Color.Cyan)
                                ) { noteId ->
                                    if(note.locked){
                                        navController.navigate(
                                            NotesScreen.LockedScreenFile.name + "?noteId=${note.noteId}&noteColor=${note.color!!}"
                                        )
                                    }else{
                                        navController.navigate(
                                            NotesScreen.AddEditNoteScreen.name + "?noteId=${note.noteId}&noteColor=${note.color!!}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomStaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    numColumns: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurable, constraints ->
        val columnWidth = (constraints.maxWidth / numColumns)
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val columnHeights = IntArray(numColumns) { 0 }

        val placeables = measurable.map { measurable ->
            // inside placeble we are creating
            // variables as column and placebles.
            val column = testColumn(columnHeights)
            val placeable = measurable.measure(itemConstraints)

            // on below line we are increasing our column height/
            columnHeights[column] += placeable.height
            placeable
        }

        val height =
            columnHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
                ?: constraints.minHeight

        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // on below line we are creating a variable for column y pointer.
            val columnYPointers = IntArray(numColumns) { 0 }

            // on below line we are setting x and y for each placeable item
            placeables.forEach { placeable ->
                // on below line we are calling test
                // column method to get our column index
                val column = testColumn(columnYPointers)

                placeable.place(
                    x = columnWidth * column,
                    y = columnYPointers[column]
                )

                // on below line we are setting
                // column y pointer and incrementing it.
                columnYPointers[column] += placeable.height
            }
        }
    }
}

private fun testColumn(columnHeights: IntArray): Int {
    // on below line we are creating a variable for min height.
    var minHeight = Int.MAX_VALUE

    // on below line we are creating a variable for column index.
    var columnIndex = 0

    // on below line we are setting column  height for each index.
    columnHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            columnIndex = index
        }
    }
    // at last we are returning our column index.
    return columnIndex
}


//@Preview
@Composable
fun Header(
    homeViewModel: HomeViewModel,
    navigate: () -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Notes",
            modifier = Modifier
                .weight(0.75f)
                .padding(8.dp),
            color = MaterialTheme.colors.onBackground,
            fontFamily = Manrope,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp
        )

        Card(
            modifier = Modifier
                .padding(8.dp)
                .size(60.dp)
                .clickable {
                    navigate()
                },
            shape = CircleShape,
            backgroundColor = MaterialTheme.colors.background,
            elevation = 2.dp
        ) {
            ImageComposable(homeViewModel = homeViewModel)
        }
    }
}

@Composable
fun ImageComposable(
    homeViewModel: HomeViewModel
) {

    val image = "https://robohash.org/${homeViewModel.imageUrl.value}.jpg?set=set4"

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image)
            .size(Size.ORIGINAL)
            .scale(Scale.FILL)
            .build(),
    )

    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer {
                rotationY = 180f
            },
        contentScale = ContentScale.Inside
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search your notes",
    enabled: Boolean = true,
    onSearch: (String) -> Unit = {},
) {
    var text by remember {
        mutableStateOf("")
    }

    val validState = remember(text) {
        text.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.black, CircleShape)
            .padding(horizontal = 3.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(text.trim())
            },
            enabled = enabled,
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = AppColors.grayLightText, fontSize = 20.sp),
            modifier = Modifier
                .padding(start = 52.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                onSearch(text.trim())
                keyboardController?.hide()
            }
        )
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search Icon",
            modifier = modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 6.dp)
                .size(28.dp)
        )

        AnimatedVisibility(visible = isHintDisplayed) {
            Text(
                text = hint,
                color = AppColors.grayText,
                modifier = modifier.padding(start = 40.dp)
            )
        }
    }
}

