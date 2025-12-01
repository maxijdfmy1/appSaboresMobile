package com.example.saboresdehogar.model.user

data class ActualizarUsuarioDto(
    val nombre: String,
    val rut: String?,
    val email: String,
    val telefono: String,
    val direccion: String
)
