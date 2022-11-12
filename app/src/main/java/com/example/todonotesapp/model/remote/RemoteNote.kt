package com.example.todonotesapp.model.remote

import androidx.compose.ui.graphics.Color

data class NoteItem(
    val noteId : String,
    val title : String,
    val description : String,
    val color : Int,
    val date : Long,
    val locked : Boolean,
    val label : String,
    val todoCheckpoint: List<NoteCheckpoints>
)


data class RegisterNote(
    val noteId : String,
    val title : String,
    val description : String,
    val color : Int,
    val date : Long,
    val locked : Boolean,
    var safePassword: String? = null,
    val label : String,
    val todoCheckpoint: List<NoteCheckpoints>
)