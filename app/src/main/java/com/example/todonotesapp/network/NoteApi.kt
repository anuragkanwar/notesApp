package com.example.todonotesapp.network

import androidx.room.Update
import com.example.todonotesapp.model.remote.*
import com.example.todonotesapp.utils.Constants.API_VERSION
import retrofit2.http.*

interface NoteApi {


    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/register_user")
    suspend fun createAccount(
        @Body user : RegisterUser
    ) : LoginRegisterResponse


    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/users/login")
    suspend fun login(
        @Body user : LoginUser
    ) : LoginRegisterResponse

    @Headers("Content-Type: application/json")
    @PUT("$API_VERSION/users/edit_profile")
    suspend fun editProfile(
        @Header("Authorization") token : String,
        @Body user : UpdateProfile
    ) : EditProfileResponse

    @Headers("Content-Type: application/json")
    @DELETE("$API_VERSION/users/delete_account")
    suspend fun deleteAccount(
        @Header("Authorization") token : String
    ) :  NoDataResponse



    // ==================NOTES======================

    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/create_note")
    suspend fun createNote(
        @Header("Authorization") token : String,
        @Body note : RegisterNote
    ) : NoDataResponse

//
    @Headers("Content-Type: application/json")
    @GET("$API_VERSION/notes/load_notes")
    suspend fun loadNotes(
        @Header("Authorization") token : String
    ) : NotesResponse


    @Headers("Content-Type: application/json")
    @PUT("$API_VERSION/notes/update_note")
    suspend fun updateNote(
        @Header("Authorization") token : String,
        @Body note : RegisterNote
    ) : NoDataResponse


    @Headers("Content-Type: application/json")
    @DELETE("$API_VERSION/notes/delete_note/{id}")
    suspend fun deleteNote(
        @Header("Authorization") token : String,
        @Path("id") id : String,
    ) : NoDataResponse


    @Headers("Content-Type: application/json")
    @DELETE("$API_VERSION/notes/delete_all_notes")
    suspend fun deleteAllNote(
        @Header("Authorization") token : String,
    ) : NoDataResponse


    @Headers("Content-Type: application/json")
    @PUT("$API_VERSION/notes/set_lock/{id}")
    suspend fun setLock(
        @Header("Authorization") token : String,
        @Path("id") id : String,
        @Body safePassword: SafePassword
    ) : NoDataResponse


    @Headers("Content-Type: application/json")
    @POST("$API_VERSION/notes/{id}")
    suspend fun getNote(
        @Header("Authorization") token : String,
        @Path("id") id : String,
        @Body safePassword: SafePassword
    ) : LockedNoteResponse

}