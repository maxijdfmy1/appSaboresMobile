package com.example.saboresdehogar.model.payment

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    TRANSFER,
    WEBPAY;

    fun getDisplayName(): String {
        return when (this) {
            CASH -> "Efectivo"
            CREDIT_CARD -> "Tarjeta de Crédito"
            DEBIT_CARD -> "Tarjeta de Débito"
            TRANSFER -> "Transferencia"
            WEBPAY -> "Webpay"
        }
    }
}