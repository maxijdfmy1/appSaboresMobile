package com.example.saboresdehogar.model.user

import com.google.gson.annotations.SerializedName

// CORRECCIÓN: El campo para la contraseña debe llamarse 'password' para coincidir con el backend.
data class LoginDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
