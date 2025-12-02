package com.example.saboresdehogar.model.user

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: String,
    @SerializedName("email") val email: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("id") val id: String
)
