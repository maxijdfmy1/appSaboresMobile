package com.example.saboresdehogar.model.user

import com.example.saboresdehogar.model.payment.PaymentMethod

data class UserPreferences(
    val userId: String,
    val notificationsEnabled: Boolean = true,
    val emailNotifications: Boolean = true,
    val smsNotifications: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val language: String = "es",
    val defaultPaymentMethod: PaymentMethod? = null
)