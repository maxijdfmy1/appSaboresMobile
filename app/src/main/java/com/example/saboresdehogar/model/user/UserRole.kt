package com.example.saboresdehogar.model.user

enum class UserRole {
    CUSTOMER,
    ADMIN,
    DELIVERY;

    fun getDisplayName(): String {
        return when (this) {
            CUSTOMER -> "Cliente"
            ADMIN -> "Administrador"
            DELIVERY -> "Repartidor"
        }
    }
}