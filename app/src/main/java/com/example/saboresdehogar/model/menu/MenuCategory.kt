package com.example.saboresdehogar.model.menu

data class MenuCategory(
    val type: CategoryType,
    val displayName: String,
    val items: List<MenuItem>
)