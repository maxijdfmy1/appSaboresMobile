package com.example.saboresdehogar.model.stock

data class StockItem(
    val id: String,
    val name: String, // Ej: "Carne", "Cebolla"
    var quantity: Int,
    val unit: String // Ej: "kg", "unidades"
)
