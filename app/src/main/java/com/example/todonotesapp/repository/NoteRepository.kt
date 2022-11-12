package com.example.todonotesapp.repository

import android.util.Log
import com.example.todonotesapp.data.DataOrException
import com.example.todonotesapp.data.NoteDao
import com.example.todonotesapp.model.local.LocalNote
import com.example.todonotesapp.model.remote.*
import com.example.todonotesapp.network.NoteApi
import com.example.todonotesapp.utils.Constants
import com.example.todonotesapp.utils.SessionManager
import com.example.todonotesapp.utils.isNetworkConnected
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.xml.transform.Result

class NoteRepository @Inject constructor(
    val noteApi: NoteApi,
    val noteDao: NoteDao,
    val sessionManager: SessionManager
) {

    suspend fun createUser(user: RegisterUser): DataOrException<ResultedUser, Boolean, Exception> {
        try {
            if (!isNetworkConnected(sessionManager.context)) {
                return DataOrException(e = Exception("No internet connection"))
            }
            val result = noteApi.createAccount(user)

            if (result.success) {
                sessionManager.updateSession(
                    token = result.data!!.token,
                    name = result.data.user.name,
                    email = result.data.user.email,
                    imageUrl = result.data.user.imageUrl
                )
                return DataOrException(data = result.data.user)
            } else {
                return DataOrException(e = Exception(result.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }


    suspend fun loginUser(user: LoginUser): DataOrException<ResultedUser, Boolean, Exception> {
        try {
            if (!isNetworkConnected(sessionManager.context)) {
                return DataOrException(e = Exception("No internet connection"))
            }
            val result = noteApi.login(user)
            return if (result.success) {

                sessionManager.updateSession(
                    token = result.data!!.token,
                    name = result.data.user.name,
                    email = result.data.user.email,
                    imageUrl = result.data.user.imageUrl
                )
                DataOrException(data = result.data.user)
            } else {
                DataOrException(e = Exception(result.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }


    suspend fun getUser(): DataOrException<ResultedUser, Boolean, Exception> {
        try {
            val name = sessionManager.getCurrentUserName()
            val email = sessionManager.getCurrentUserEmail()
            val imageUrl = sessionManager.getCurrentImageUrl()

            if (name == null || email == null) {
                return DataOrException(e = Exception("user not Logged In!"))
            }
            return DataOrException(
                data = ResultedUser(
                    name = name,
                    email = email,
                    imageUrl = imageUrl!!
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }

    suspend fun getImageIcon(): DataOrException<String, Boolean, Exception> {
        try {
            val imageUrl = sessionManager.getCurrentImageUrl()
                ?: return DataOrException(data = Constants.ICON_STRING)

            return DataOrException(
                data = imageUrl
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }


    suspend fun logout(): DataOrException<String, Boolean, Exception> {
        return try {
            sessionManager.logout()
            DataOrException(data = "Logged Out Successfully!!")
        } catch (e: Exception) {
            e.printStackTrace()
            DataOrException(e = e)
        }
    }

    suspend fun deleteAccount(): DataOrException<String, Boolean, Exception> {
        return try {

            if (!isNetworkConnected(sessionManager.context)) {
                return DataOrException(e = Exception("No Internet connection"))
            }
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(e = Exception("user not Logged In..."))

            val response = noteApi.deleteAccount(token = "Bearer $token")
            if (response.success) {
                sessionManager.logout()
                DataOrException(data = response.message)
            } else {
                DataOrException(e = Exception(response.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DataOrException(e = e)
        }
    }

    suspend fun editProfile(
        name: String,
        email: String,
        imageUrl: String
    ): DataOrException<ResultedUser, Boolean, Exception> {
        try {
            if (!isNetworkConnected(sessionManager.context)) {
                return DataOrException(e = Exception("No Internet connection"))
            }
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(e = Exception("user not Logged In..."))
            val response = noteApi.editProfile(
                token = "Bearer $token",
                UpdateProfile(name = name, email = email, imageUrl = imageUrl)
            )
            if (response.success) {
                return DataOrException(data = response.data)
            } else {
                return DataOrException(e = Exception(response.message.toString()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }


    // ===================== NOTES ============================


    suspend fun createNote(note: LocalNote,safePassword: String?): DataOrException<String, Boolean, Exception> {
        try {

            if(!note.locked) {
                noteDao.insertNote(note)
            }
            else{
                val token = sessionManager.getCurrentToken() ?: return DataOrException(e = Exception("user Not logged In..."))
                if(!isNetworkConnected(sessionManager.context)){
                    return DataOrException(e = Exception("No Internet Connection!"))
                }
            }
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(data = "Note is Saved in Local Database")

            if(!isNetworkConnected(sessionManager.context)){
                return DataOrException(e = Exception("No Internet Connection!"))
            }


            val result = noteApi.createNote(
                "Bearer $token",
                RegisterNote(
                    noteId = note.noteId,
                    title = note.noteTitle!!,
                    description = note.description!!,
                    color = note.color!!,
                    date = note.date!!,
                    locked = note.locked,
                    safePassword = safePassword,
                    label = note.label!!,
                    todoCheckpoint = note.checkpoints
                )
            )

            if (result.success) {
                noteDao.insertNote(note.also { it.connected = true })
                return DataOrException(data = "Note saved on server")
            } else {
                return DataOrException(e = Exception(result.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }

    suspend fun updateNote(note: LocalNote): DataOrException<String, Boolean, Exception> {
        try {
            noteDao.insertNote(note)
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(data = "Note is Updated in Local Database")

            if(!isNetworkConnected(sessionManager.context)){
                return DataOrException(e = Exception("No Internet Connection!"))
            }

            val result = noteApi.updateNote(
                "Bearer $token",
                RegisterNote(
                    noteId = note.noteId,
                    title = note.noteTitle!!,
                    description = note.description!!,
                    color = note.color!!,
                    date = note.date!!,
                    locked = note.locked,
                    label = note.label!!,
                    todoCheckpoint = note.checkpoints
                )
            )

            if (result.success) {
                noteDao.insertNote(note.also { it.connected = true })
                return DataOrException(data = "Note updated successfully!!")
            } else {
                return DataOrException(e = Exception(result.message))
            }
        } catch (e: Exception) {

            e.printStackTrace()
            return DataOrException(e = e)
        }
    }

    suspend fun getNoteById(noteId: String) : DataOrException<LocalNote,Boolean,Exception>{
        try {
            val result = noteDao.getNoteById(noteId)
            return DataOrException(data = result)
        }catch (e : Exception){
            return DataOrException(e = e)
        }
    }

    suspend fun getLockedNoteById(noteId: String,safePassword: SafePassword) : DataOrException<NoteItem,Boolean,Exception>{
        try {
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(e = Exception("user Not Logged In.."))

            if(!isNetworkConnected(sessionManager.context)){
                return DataOrException(e = Exception("No Internet Connection!"))
            }


            val result = noteApi.getNote(
                "Bearer $token",
                id = noteId,
                safePassword = safePassword
            )

            if (result.success) {
                return DataOrException(data = result.data)
            } else {
                return DataOrException(e = Exception(result.message))
            }
        }catch (e : Exception){
            e.printStackTrace()
            return DataOrException(e = e)
        }
    }

    suspend fun setTheLock(noteId: String,safePassword: SafePassword) : DataOrException<String,Boolean,Exception>{
        try {
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(e = Exception("user Not Logged In.."))

            if(!isNetworkConnected(sessionManager.context)){
                return DataOrException(e = Exception("No Internet Connection!"))
            }

            val result = noteApi.setLock(
                token = "Bearer $token",
                id = noteId,
                safePassword = safePassword
            )

            if (result.success) {
                val note = noteDao.getNoteById(noteId)
                noteDao.insertNote(note.also { it.locked = true })
                return DataOrException(data = "Locked..")
            } else {
                return DataOrException(e = Exception(result.message))
            }
        }catch (e : Exception){
            return DataOrException(e = e)
        }
    }


    suspend fun deleteNote(noteId: String): DataOrException<String, Boolean, Exception> {
        try {
            noteDao.deleteNoteLocally(noteId)
            val token = sessionManager.getCurrentToken()
                ?: return DataOrException(data = "Note is Deleted in Local Database")

            if(!isNetworkConnected(sessionManager.context)){
                return DataOrException(e = Exception("No Internet Connection!"))
            }

            val result = noteApi.deleteNote(
                token = "Bearer $token",
                id = noteId
            )

            if (result.success) {
                noteDao.deleteNote(noteId)
                return DataOrException(data = "Note deleted successfully!!")
            } else {
                return DataOrException(e = Exception(result.message))
            }
        } catch (e: Exception) {
            return DataOrException(e = e)
        }
    }


    fun getAllNotes() : Flow<List<LocalNote>> = noteDao.getAllNotesOrderedByDate()

    suspend fun syncNotes(){
        try {

            val token = sessionManager.getCurrentToken() ?: return


            if(!isNetworkConnected(sessionManager.context))
                return

            val locallyDeletedNotes = noteDao.getAllLocallyDeletedNotes()


            locallyDeletedNotes.forEach {
                deleteNote(it.noteId)
            }

            val notConnectedNotes = noteDao.getAllLocalNotes()


            notConnectedNotes.forEach {
                createNote(it,null)
            }

            val notUpdatedNotes = noteDao.getAllLocalNotes()
            notUpdatedNotes.forEach {
                updateNote(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun isConnected() : Boolean{
        try {
            val token = sessionManager.getCurrentToken() ?: return false
            return true
        }catch (e : Exception){
            return false
        }
    }

    suspend fun getAllNotesFromServer(){
        try{
            val token = sessionManager.getCurrentToken() ?: return
            if(!isNetworkConnected(sessionManager.context))
                return

            val result = noteApi.loadNotes(
                "Bearer $token"
            )

            if(result.success){
                result.data.forEach { remoteNote->
                    noteDao.insertNote(
                        LocalNote(
                            noteId = remoteNote.noteId,
                            noteTitle = remoteNote.title,
                            description = remoteNote.description,
                            color = remoteNote.color,
                            date = remoteNote.date,
                            locked = remoteNote.locked,
                            label = remoteNote.label,
                            checkpoints = remoteNote.todoCheckpoint,
                            connected = true
                        )
                    )
                }
            }
        }catch (e : Exception){
            e.printStackTrace()
            return
        }
    }

}