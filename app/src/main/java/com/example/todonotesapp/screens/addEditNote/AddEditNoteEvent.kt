package com.example.todonotesapp.screens.addEditNote

import androidx.compose.ui.focus.FocusState
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.NoteCheckpoints
import com.example.todonotesapp.model.remote.SafePassword
import com.example.todonotesapp.screens.notes.NotesEvent

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value : String) :  AddEditNoteEvent()
    data class EnteredContent(val value : String) :  AddEditNoteEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : AddEditNoteEvent()
    data class ChangeContentFocus(val focusState: FocusState) : AddEditNoteEvent()

    data class ChangeColor(val color: Int) : AddEditNoteEvent()
    data class ChangeLabel(val label: String) : AddEditNoteEvent()

    data class SetUnsetLock(val safePassword: String?) : AddEditNoteEvent()

    data class DeleteCheckPoint(val ckpt: NoteCheckpoints) : AddEditNoteEvent()
    data class AddCheckPoint(val ckpt: NoteCheckpoints) : AddEditNoteEvent()
    data class ChangeCheckpoint(val ckpt : NoteCheckpoints) : AddEditNoteEvent()
    data class LockedNote(val noteId : String,val safePassword: SafePassword) : AddEditNoteEvent()

//    data class DeleteNote(val noteId: String) : AddEditNoteEvent()
//    object RestoreNote : AddEditNoteEvent()

    object SaveNote : AddEditNoteEvent()
}
