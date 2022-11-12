package com.example.todonotesapp.screens.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.model.remote.NoteItem
import com.example.todonotesapp.repository.NoteRepository
import com.example.todonotesapp.screens.notes.NotesEvent
import com.example.todonotesapp.screens.notes.NotesState
import com.example.todonotesapp.screens.notes.UiEvent
import com.example.todonotesapp.utils.Constants
import com.example.todonotesapp.utils.util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {

    private val _imageUrl = mutableStateOf(
        Constants.ICON_STRING
    )
    val imageUrl : State<String> = _imageUrl


    fun getImageIcon() {
        viewModelScope.launch {
            val result = repository.getImageIcon()

            if (result.data != null) {
                _imageUrl.value = result.data.toString()
            }
        }
    }

    init {
        syncNotes()
        getNotes()
    }

    private var getNotesJob:  Job? = null

    private val _state = mutableStateOf(NotesState())
    val state : State<NotesState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var recentlyDeletedNote: LocalNote? = null

    fun onEvent(event : NotesEvent){
        when(event) {
            is NotesEvent.SearchNote->{
                searchNotes(event.query)
            }
            is NotesEvent.OrderNotes->{
                getNotes()
            }
            is NotesEvent.DeleteNote->{
                viewModelScope.launch {
                    repository.deleteNote(event.note.noteId)
                    recentlyDeletedNote = event.note
                }
            }
            is NotesEvent.RestoreNote->{
                viewModelScope.launch{
                    if(recentlyDeletedNote?.locked == true){
                        _eventFlow.emit(UiEvent.ShowSnackbar(message = "Locked Note can't be retrieved"))
                        return@launch
                    }
                    repository.createNote(recentlyDeletedNote ?: return@launch,null)
                    recentlyDeletedNote = null
                }
            }
        }
    }

    private fun getNotes() {
        getNotesJob?.cancel()
        getNotesJob = repository.getAllNotes()
            .onEach { notes->
                _state.value = state.value.copy(notes = notes)
            }
            .launchIn(viewModelScope)
    }

    private fun searchNotes(query: String){
        val notes = _state.value.notes.filter {
            (it.noteTitle?.contains(query,true) == true || it.description?.contains(query,true) == true || it.label?.contains(query,true) == true)
        }

        val unlockedNotes = notes.filter {
            !it.locked
        }

        _state.value = state.value.copy(notes = unlockedNotes)
    }

    fun syncNotes(
        onDone : (() -> Unit)? = null
    ){
        viewModelScope.launch {
            repository.syncNotes()
            onDone?.invoke()
        }
    }
}