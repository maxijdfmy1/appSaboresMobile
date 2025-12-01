package com.example.saboresdehogar.model.user

data class LoginResponse(
    val mensaje: String,
    val usuario: String,
    val email: String,
    val rol: String,
    val id: String
)
