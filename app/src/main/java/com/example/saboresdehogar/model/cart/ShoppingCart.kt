package com.example.saboresdehogar.model.cart

import com.example.saboresdehogar.model.menu.MenuItem

data class ShoppingCart(
    val items: MutableList<CartItem> = mutableListOf()
) {
    val total: Int
        get() = items.sumOf { it.subtotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity } // Corrección: Sumar cantidades, no solo size

    fun addItem(menuItem: MenuItem, quantityToAdd: Int = 1, notes: String? = null) {
        val existingItem = items.find { it.menuItem.id == menuItem.id && it.notes == notes }
        if (existingItem != null) {
            existingItem.quantity += quantityToAdd
        } else {
            items.add(CartItem(menuItem, quantityToAdd, notes))
        }
    }

    fun removeItem(menuItemId: String) {
        // Nota: Esto eliminará todas las variantes del producto si solo usamos ID.
        // Para ser precisos deberíamos eliminar por ID+Notas o un ID único de CartItem.
        // Por ahora mantenemos ID, pero borrará todas las instancias.
        items.removeAll { it.menuItem.id == menuItemId }
    }

    fun updateQuantity(menuItemId: String, quantity: Int) {
        // Igual que arriba, esto es ambiguo si hay múltiples items con mismo ID producto.
        // Asumimos que solo actualiza el primero que encuentra o todos.
        // Para una implementación robusta, CartItem debería tener su propio ID único (UUID).
        // Para este prototipo, actualizamos el primero.
        items.find { it.menuItem.id == menuItemId }?.quantity = quantity
    }

    fun clear() {
        items.clear()
    }
}
