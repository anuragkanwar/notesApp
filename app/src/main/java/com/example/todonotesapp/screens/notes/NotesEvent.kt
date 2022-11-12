package com.example.todonotesapp.screens.notes

import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.SafePassword

sealed class NotesEvent {
    object OrderNotes : NotesEvent()
//    data class LockedNote(val note : LocalNote,val safePassword: SafePassword) : NotesEvent()
    data class DeleteNote(val note: LocalNote) : NotesEvent()
    object RestoreNote : NotesEvent()
    data class SearchNote(val query : String) : NotesEvent()
}

sealed class UiEvent {
    object Loading : UiEvent()
    data class ShowSnackbar(val message: String) : UiEvent()
    object SaveNote : UiEvent()
    object LockedNote : UiEvent()
//    object Success : UiEvent()
}