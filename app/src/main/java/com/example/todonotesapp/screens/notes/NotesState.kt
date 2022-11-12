package com.example.todonotesapp.screens.notes

import com.example.todonotesapp.model.local.LocalNote

data class NotesState(
    val notes : List<LocalNote> = emptyList(),
    val isGroupByLabel : Boolean = false
)
