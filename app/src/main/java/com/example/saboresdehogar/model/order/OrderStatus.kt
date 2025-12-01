package com.example.saboresdehogar.model.order

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED;

    fun getDisplayName(): String {
        return when (this) {
            PENDING -> "Pendiente"
            CONFIRMED -> "Confirmado"
            PREPARING -> "En Preparación"
            READY -> "Listo"
            DELIVERED -> "Entregado"
            CANCELLED -> "Cancelado"
        }
    }
}