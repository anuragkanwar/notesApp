package com.example.todonotesapp.model.remote

data class RegisterUser(
    val email: String,
    val name: String,
    val password: String,
    val imageUrl: String
)

data class LoginUser(
    val email: String,
    val password: String
)

data class ResultedUser(
    val name: String,
    val email: String,
    val imageUrl: String
)

data class User(
    val userId: Int,
    val email: String,
    val name: String,
    val password: String,
    val safePassword: String?,
    val imageUrl: String
)

data class UpdateProfile(
    val name: String,
    val email: String,
    val imageUrl: String,
)