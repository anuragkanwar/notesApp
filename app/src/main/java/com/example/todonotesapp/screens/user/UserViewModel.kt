package com.example.todonotesapp.screens.user

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.todonotesapp.model.remote.LoginUser
import com.example.todonotesapp.model.remote.RegisterUser
import com.example.todonotesapp.model.remote.UpdateProfile
import com.example.todonotesapp.repository.NoteRepository
import com.example.todonotesapp.screens.BaseViewModel
import com.example.todonotesapp.screens.ResponseEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: NoteRepository) :
    BaseViewModel<ResponseEvent>() {

    var isLoading = mutableStateOf(false)

    var isSuccess = mutableStateOf(false)

    var loadError = mutableStateOf("")

    fun getCurrentUser() {
        viewModelScope.launch {
            isLoading.value = true
            isSuccess.value = false
            val result = repository.getUser()
            if (result.data == null) {
                loadError.value = "Error : ${result.e}"
                isLoading.value = false
            } else {
                loadError.value = ""
                isLoading.value = false
                isSuccess.value = true
            }
        }
    }


    fun loginUser(user: LoginUser) {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.loginUser(user)
            if (result.data == null) {
                isLoading.value = false
                sendEvent(ResponseEvent.Failure(result.e.toString()))
            } else {
                isLoading.value = false
                repository.syncNotes()
                repository.getAllNotesFromServer()
                sendEvent(ResponseEvent.Success)
            }
        }
    }

    fun createUser(user: RegisterUser) {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.createUser(user)
            if (result.data == null) {
                isLoading.value = false
                repository.syncNotes()
                sendEvent(ResponseEvent.Failure(result.e.toString()))
            } else {
                isLoading.value = false
                sendEvent(ResponseEvent.Success)
            }
        }
    }

    fun editProfile(user: UpdateProfile) {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.editProfile(user.name, user.email, user.imageUrl)
            if (result.data == null) {
                isLoading.value = false
                sendEvent(ResponseEvent.Failure(result.e.toString()))
            } else {
                isLoading.value = false
                sendEvent(ResponseEvent.Success)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.logout()
            if (result.data == null) {
                isLoading.value = false
                sendEvent(ResponseEvent.Failure(result.e.toString()))
            } else {
                isLoading.value = false
                sendEvent(ResponseEvent.Success)
            }
        }
    }

}