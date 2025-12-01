package com.example.saboresdehogar.model.order

import com.example.saboresdehogar.model.cart.CartItem

data class Order(
    val id: String,
    val items: List<CartItem>,
    val total: Int,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String? = null,
    val orderType: OrderType,
    val status: OrderStatus = OrderStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val userId: String? = null
)