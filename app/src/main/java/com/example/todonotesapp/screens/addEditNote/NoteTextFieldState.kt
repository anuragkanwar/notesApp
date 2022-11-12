package com.example.todonotesapp.screens.addEditNote

import com.example.todonotesapp.model.remote.NoteCheckpoints

data class NoteTextFieldState(
    val text : String = "",
    val hintText : String = "",
    val isHintVisible : Boolean = true
)

data class CheckPointState(
    val checkpoints : MutableList<NoteCheckpoints> = mutableListOf()
)