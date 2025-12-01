package com.example.saboresdehogar.model.payment

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED;

    fun getDisplayName(): String {
        return when (this) {
            PENDING -> "Pendiente"
            PROCESSING -> "Procesando"
            COMPLETED -> "Completado"
            FAILED -> "Fallido"
            REFUNDED -> "Reembolsado"
        }
    }
}