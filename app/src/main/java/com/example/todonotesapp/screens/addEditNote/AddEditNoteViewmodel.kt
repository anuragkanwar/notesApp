package com.example.todonotesapp.screens.addEditNote

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.model.remote.SafePassword
import com.example.todonotesapp.repository.NoteRepository
import com.example.todonotesapp.screens.notes.NotesEvent
import com.example.todonotesapp.screens.notes.NotesState
import com.example.todonotesapp.screens.notes.UiEvent
import com.example.todonotesapp.utils.isNetworkConnected
import com.example.todonotesapp.utils.util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hintText = "Enter Title..."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hintText = "Enter Some Content..."
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf(LocalNote.noteColors.first().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _noteLocked = mutableStateOf(false)
    val noteLocked: State<Boolean> = _noteLocked

    private var safePassword: String? = null

    private val _noteLabel = mutableStateOf(LocalNote.noteLabels.first())
    val noteLabel: State<String> = _noteLabel

    private val _noteConnected = mutableStateOf(false)
    val noteConnected : State<Boolean> = _noteConnected

    var checkpoints by mutableStateOf(listOf<NoteCheckpoints>())


    var currentNoteId: String? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var isInitiallyLocked : Boolean = false


    init {
        savedStateHandle.get<String>("noteId")?.let { noteId ->
            if (noteId != "-1") {
                viewModelScope.launch {
                    val result = repository.getNoteById(noteId)
                    if (result.data != null) {
                        currentNoteId = result.data!!.noteId
                        _noteTitle.value = noteTitle.value.copy(
                            text = result.data!!.noteTitle!!,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = result.data!!.description!!,
                            isHintVisible = false
                        )
                        _noteColor.value = result.data!!.color!!
                        _noteLabel.value = result.data!!.label!!
                        _noteLocked.value = result.data!!.locked
                        checkpoints = checkpoints + result.data!!.checkpoints

                        isInitiallyLocked = result.data!!.locked
                    }else{
                        isInitiallyLocked = false
                    }
                }
            }
        }

        viewModelScope.launch {
            val result = repository.isConnected()
            _noteConnected.value = result
        }
    }


    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = _noteTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = _noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = _noteContent.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = _noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeCheckpoint -> {
                checkpoints = checkpoints.toMutableList().also {
                    it.remove(event.ckpt)
                }
                checkpoints = checkpoints + event.ckpt
            }
            is AddEditNoteEvent.AddCheckPoint -> {
                checkpoints = checkpoints + event.ckpt
            }
            is AddEditNoteEvent.DeleteCheckPoint -> {
                checkpoints = checkpoints.toMutableList().also {
                    it.remove(event.ckpt)
                }
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.ChangeLabel -> {
                _noteLabel.value = event.label
            }
            is AddEditNoteEvent.SetUnsetLock -> {
                _noteLocked.value = !noteLocked.value
                if (noteLocked.value) {
                    safePassword = event.safePassword
                } else {
                    safePassword = event.safePassword
                }
            }
            is AddEditNoteEvent.LockedNote -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val result = repository.getLockedNoteById(
                                noteId = event.noteId,
                                safePassword = event.safePassword
                            )
                            Log.d("TAG", "VIEWMODEL : ${result.data}")
                            if (result.data != null) {
                                _eventFlow.emit(UiEvent.LockedNote)
                            }
                        } catch (e: Exception) {
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message ?: "Can't get this note..."
                                )
                            )
                        }
                    }
                }
            }
            is AddEditNoteEvent.SaveNote -> {

                viewModelScope.launch {
                    try {
                        if (currentNoteId == null) {
                            _eventFlow.emit(UiEvent.Loading)
                            val result = repository.createNote(
                                LocalNote(
                                    noteTitle = noteTitle.value.text.trim(),
                                    description = noteContent.value.text.trim(),
                                    color = noteColor.value,
                                    date = System.currentTimeMillis(),
                                    label = noteLabel.value,
                                    locked = noteLocked.value,
                                    checkpoints = checkpoints
                                ),
                                safePassword = safePassword
                            )
                            if (result.data == null) {
                                _eventFlow.emit(
                                    UiEvent.ShowSnackbar(
                                        message = result.e.toString() ?: "Couldn't save note!"
                                    )
                                )
                                return@launch
                            }
                            _eventFlow.emit(UiEvent.SaveNote)
                        } else {
                            _eventFlow.emit(UiEvent.Loading)
                            val result = repository.updateNote(
                                LocalNote(
                                    noteId = currentNoteId.toString(),
                                    noteTitle = noteTitle.value.text.trim(),
                                    description = noteContent.value.text.trim(),
                                    color = noteColor.value,
                                    date = System.currentTimeMillis(),
                                    locked = if(isInitiallyLocked == true && noteLocked.value == false) false else isInitiallyLocked,
                                    label = noteLabel.value,
                                    checkpoints = checkpoints
                                )
                            )

                            if (result.data == null) {
                                _eventFlow.emit(
                                    UiEvent.ShowSnackbar(
                                        message = result.e.toString() ?: "Couldn't save note!"
                                    )
                                )
                                return@launch
                            }
                            if (!isInitiallyLocked && noteLocked.value) {
                                val result = repository.setTheLock(
                                    currentNoteId.toString(),
                                    SafePassword(safePassword)
                                )
                                if (result.data == null) {
                                    _eventFlow.emit(
                                        UiEvent.ShowSnackbar(
                                            message = result.e.toString() ?: "Couldn't save note!"
                                        )
                                    )
                                    return@launch
                                }
                            }
                            _eventFlow.emit(UiEvent.SaveNote)
                        }
                    } catch (e: Exception) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note!"
                            )
                        )
                    }
                }
            }
        }
    }
}