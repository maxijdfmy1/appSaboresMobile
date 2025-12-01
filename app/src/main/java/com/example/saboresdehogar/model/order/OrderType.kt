package com.example.saboresdehogar.model.order

enum class OrderType {
    DELIVERY,
    PICKUP,
    DINE_IN;

    fun getDisplayName(): String {
        return when (this) {
            DELIVERY -> "Delivery"
            PICKUP -> "Retiro en Local"
            DINE_IN -> "Para Comer Aquí"
        }
    }
}