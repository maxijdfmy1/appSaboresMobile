package com.example.saboresdehogar.model.auth

import com.example.saboresdehogar.model.user.User

data class UserSession(
    val user: User,
    val token: String,
    val refreshToken: String? = null,
    val expiresAt: Long,
    val loginTimestamp: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    fun isAboutToExpire(minutesThreshold: Int = 5): Boolean {
        val threshold = minutesThreshold * 60 * 1000
        return (expiresAt - System.currentTimeMillis()) < threshold
    }
}