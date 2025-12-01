package com.example.saboresdehogar.model.auth

import com.example.saboresdehogar.model.user.User

data class AuthResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val expiresAt: Long? = null,
    val message: String? = null,
    val errorCode: String? = null
)