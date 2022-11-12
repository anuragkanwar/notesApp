package com.example.todonotesapp.model.remote

data class LoginRegisterResponse(
    val success : Boolean,
    val message : String,
    val data : UserResponse?
)

data class EditProfileResponse(
    val success : Boolean,
    val message : String,
    val data : ResultedUser?
)

data class NoDataResponse(
    val success: Boolean,
    val message: String,
    val data : String? = null
)

data class NotesResponse(
    val success: Boolean,
    val message: String,
    val data : List<NoteItem>
)

data class LockedNoteResponse(
    val success: Boolean,
    val message: String,
    val data : NoteItem
)

