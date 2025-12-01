package com.example.saboresdehogar.model.order

data class OrderHistory(
    val userId: String,
    val orders: List<Order>,
    val totalOrders: Int,
    val totalSpent: Double
)