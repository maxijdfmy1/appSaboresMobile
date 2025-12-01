package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.model.cart.ShoppingCart
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.data.source.local.LocalDataSource

class CartRepository(private val localDataSource: LocalDataSource) {

    /**
     * Obtiene el carrito actual
     */
    fun getCart(): ShoppingCart {
        return localDataSource.getCart()
    }

    /**
     * Agrega un item al carrito
     */
    fun addItem(item: MenuItem, quantity: Int = 1, notes: String? = null) {
        val cart = getCart()
        cart.addItem(item, quantity, notes)
        localDataSource.saveCart(cart)
    }

    /**
     * Elimina un item del carrito
     */
    fun removeItem(itemId: String) {
        val cart = getCart()
        cart.removeItem(itemId)
        localDataSource.saveCart(cart)
    }

    /**
     * Actualiza la cantidad de un item
     */
    fun updateQuantity(itemId: String, quantity: Int) {
        val cart = getCart()
        if (quantity <= 0) {
            cart.removeItem(itemId)
        } else {
            cart.updateQuantity(itemId, quantity)
        }
        localDataSource.saveCart(cart)
    }

    /**
     * Limpia el carrito
     */
    fun clearCart() {
        localDataSource.clearCart()
    }

    /**
     * Obtiene el total del carrito
     */
    fun getCartTotal(): Int {
        return getCart().total
    }

    /**
     * Obtiene la cantidad de items
     */
    fun getItemCount(): Int {
        return getCart().itemCount
    }
}
