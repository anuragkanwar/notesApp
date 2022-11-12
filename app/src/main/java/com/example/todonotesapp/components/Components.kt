package com.example.todonotesapp.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.todonotesapp.R
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.model.remote.NoteItem
import com.example.todonotesapp.ui.theme.Manrope
import com.example.todonotesapp.utils.AppColors
import com.example.todonotesapp.utils.util
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.sin

@Composable
fun LottieComponent(
    asset: String,
    modifier: Modifier = Modifier,
    speed: Float = 1.5f,
    size: Dp = 100.dp
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Asset(asset))
    LottieAnimation(
        composition = composition,
        iterations = Int.MAX_VALUE,
        modifier = Modifier
            .padding(8.dp)
            .size(size),
        speed = speed
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputText(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    maxLine: Int = 1,
    onTextChange: (String) -> Unit,
    onImeAction: () -> Unit = {}
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.primary,
            focusedLabelColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.onBackground,
        ),
        maxLines = maxLine,
        label = {
            Text(text = label)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone =
        {
            onImeAction()
            keyboardController?.hide()
        }),
        modifier = modifier.padding(8.dp)
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputPasswordText(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    maxLine: Int = 1,
    onTextChange: (String) -> Unit,
    onImeAction: () -> Unit = {}
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by rememberSaveable { mutableStateOf(false) }


    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.primary,
            focusedLabelColor = MaterialTheme.colors.primary,
            textColor = MaterialTheme.colors.onBackground,
            unfocusedLabelColor = MaterialTheme.colors.onBackground,
        ),
        maxLines = maxLine,
        label = {
            Text(text = label)
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Outlined.Visibility
            else Icons.Outlined.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description, tint = MaterialTheme.colors.onBackground)
            }
        },
        keyboardActions = KeyboardActions(onDone =
        {
            onImeAction()
            keyboardController?.hide()
        }),
        modifier = modifier.padding(8.dp)
    )
}


@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        enabled = enabled,
        modifier = modifier.padding(10.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onBackground
        )
    ) {
        Text(text = text, modifier = Modifier.padding(6.dp))
    }

}


@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LottieComponent(asset = "loading.json", speed = 1.5f)
    }
}

@Composable
fun ErrorRetryIndicator(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        MyButton(
            text = "Retry",
            onClick = { onRetry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: LocalNote = LocalNote.sampleLocalNote,
    onClick: (String) -> Unit = {}
) {


    var maxLines = (Math.random() * (6 - 2 + 1)).toInt() + 2
    var checkPointItems = (Math.random() * (6 - 3 + 1)).toInt() + 3

    if (note.checkpoints.isEmpty()) {
        maxLines = 7
    } else {
        maxLines = 3
    }

    if (note.description!! == "") {
        checkPointItems = 6
    } else {
        checkPointItems = 4
    }


    checkPointItems = kotlin.math.min(checkPointItems, note.checkpoints.size)

    val ckptList = note.checkpoints.take(checkPointItems)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clickable {
                onClick(note.noteId)
            },
        shape = RoundedCornerShape(25.dp),
        backgroundColor = Color(note.color!!),
        elevation = 0.dp
    ) {
        if (!note.locked) {
            Column(
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {

                HeadingText(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = note.noteTitle!!,
                    color = AppColors.white
                )

                Spacer(modifier = Modifier.height(5.dp))

                if (note.description!!.isNotEmpty()) {
                    TextDescription(
                        modifier = Modifier,
                        description = note.description!!,
                        maxLines = maxLines,
                        color = AppColors.white
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }

                Column(
                    modifier = Modifier.padding(2.dp)
                ) {
                    ckptList.forEach {
                        CheckpointItem(
                            ckpt = it,
                            color = AppColors.white
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FooterComposable(
                    label = note.label!!,
                    date = util.dateFromTimeStamp(note.date),
                    color = AppColors.white
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icons8_lock_orientation),
                    contentDescription = "Locked",
                    tint = Color(
                        ColorUtils.blendARGB(
                            note.color!!,
                            0x000000,
                            0.2f
                        )
                    ),
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}


@Composable
fun FooterComposable(
    modifier: Modifier = Modifier,
    label: String,
    date: Date,
    color: Color = AppColors.blackbg
) {

    val noteDay = date.toString().split(' ')[0]
    val noteMonth = date.toString().split(' ')[1].lowercase()
    val noteDate = date.toString().split(' ')[2]
    val noteTime = date.toString().split(' ')[3]
    val noteYear = date.toString().split(' ').last()

    val current = LocalDateTime.now()

    val currYear = current.year.toString()
    val month = current.month.toString().lowercase()
    val day = current.dayOfMonth.toString()

    var displayDate = "$noteDay, "



    if (noteYear == currYear && noteDate == day && noteMonth == month.slice(0..2)) {
        displayDate += noteTime.split(":")[0] + ":"
        displayDate += noteTime.split(":")[1]
    } else {
        displayDate += noteMonth + " " + noteDate
    }


    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val formatted = current.format(formatter)


    Row(
        modifier = Modifier
            .padding(start = 8.dp, end = 6.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .border(1.dp, color, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = color
                )
            }
        }

        Text(
            text = displayDate,
            maxLines = 1,
            fontFamily = Manrope,
            fontWeight = FontWeight.Light,
            fontSize = 18.sp,
            color = color,
            modifier = Modifier.padding(end = 5.dp)
        )
    }
}

@Composable
fun CheckpointItem(
    modifier: Modifier = Modifier,
    ckpt: NoteCheckpoints,
    color: Color = AppColors.blackbg,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (ckpt.checked) painterResource(id = R.drawable.ic_icons8_filled_checkmark) else painterResource(
                id = R.drawable.icons8_circled_thin_50
            ),
            contentDescription = "Checkpoint",
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = ckpt.content,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(textDecoration = if (ckpt.checked) (TextDecoration.LineThrough) else TextDecoration.None),
            fontFamily = Manrope,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = color
        )
        Spacer(modifier = Modifier.width(3.dp))
    }
}

@Composable
fun TextDescription(
    modifier: Modifier = Modifier,
    description: String,
    color: Color = AppColors.blackbg,
    maxLines: Int = 3
) {
    Text(
        text = description,
        color = color,
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .padding(
                top = 2.dp,
                start = 4.dp,
                end = 4.dp,
                bottom = 3.dp
            )
    )
}

@Composable
fun HeadingText(
    modifier: Modifier = Modifier,
    title: String,
    color: Color = AppColors.blackbg
) {
    Text(
        text = title,
        color = color,
        fontFamily = Manrope,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 23.sp,
        modifier = modifier.padding(start = 4.dp, top = 6.dp)
    )
}


@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                }
        )
        if (isHintVisible) {
            Text(text = hint, style = textStyle, color = Color.DarkGray)
        }
    }
}


@Composable
fun CompleteDialogContent(
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
                if(text.value.trim().isNotEmpty()) {
                    onAdd(text.value)
                    dialogState.value = false
                }
            },
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = successButtonText, fontSize = 20.sp)
        }
    }
}