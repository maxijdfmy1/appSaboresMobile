package com.example.saboresdehogar.model.payment

data class PaymentInfo(
    val orderId: String,
    val method: PaymentMethod,
    val amount: Double,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)