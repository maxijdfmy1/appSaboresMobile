package com.example.saboresdehogar.model.cart

import com.example.saboresdehogar.model.menu.MenuItem

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1,
    val notes: String? = null // Notas o ingredientes excluidos
) {
    val subtotal: Int
        get() = menuItem.price * quantity
}
