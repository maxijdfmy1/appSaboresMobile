package com.example.saboresdehogar.data.source.local

import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.cart.ShoppingCart
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.auth.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalDataSource(private val jsonDataSource: JsonDataSource) {

    private val gson = Gson()

    // ============================================
    // MENÚ
    // ============================================

    /**
     * Obtiene todos los items del menú desde menu_items.json
     */
    fun getMenuItems(): List<MenuItem>? {
        val typeToken = object : TypeToken<List<MenuItem>>() {}
        return jsonDataSource.readJsonFromAssets("menu_items.json", typeToken)
    }

    // ============================================
    // USUARIOS
    // ============================================

    /**
     * Obtiene la lista de usuarios desde users.json
     */
    fun getUsers(): List<User>? {
        val typeToken = object : TypeToken<List<User>>() {}
        return jsonDataSource.readJsonFromAssets("users.json", typeToken)
    }

    /**
     * Busca un usuario por email
     */
    fun getUserByEmail(email: String): User? {
        return getUsers()?.firstOrNull { it.email == email }
    }

    // ============================================
    // SESIÓN
    // ============================================

    /**
     * Guarda la sesión del usuario
     */
    fun saveUserSession(session: UserSession) {
        val sessionJson = gson.toJson(session)
        jsonDataSource.saveToPreferences("user_session", sessionJson)
    }

    /**
     * Obtiene la sesión actual
     */
    fun getUserSession(): UserSession? {
        val sessionJson = jsonDataSource.readFromPreferences("user_session")
        return sessionJson?.let {
            gson.fromJson(it, UserSession::class.java)
        }
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isUserLoggedIn(): Boolean {
        val session = getUserSession()
        return session != null && !session.isExpired()
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        jsonDataSource.removeFromPreferences("user_session")
    }

    // ============================================
    // CARRITO
    // ============================================

    /**
     * Guarda el carrito de compras
     */
    fun saveCart(cart: ShoppingCart) {
        val cartJson = gson.toJson(cart)
        jsonDataSource.saveToPreferences("shopping_cart", cartJson)
    }

    /**
     * Obtiene el carrito guardado
     */
    fun getCart(): ShoppingCart {
        val cartJson = jsonDataSource.readFromPreferences("shopping_cart")
        return cartJson?.let {
            gson.fromJson(it, ShoppingCart::class.java)
        } ?: ShoppingCart()
    }

    /**
     * Limpia el carrito
     */
    fun clearCart() {
        jsonDataSource.removeFromPreferences("shopping_cart")
    }

    // ============================================
    // ÓRDENES
    // ============================================

    /**
     * Guarda una orden (se agrega a la lista existente)
     */
    fun saveOrder(order: Order) {
        val orders = getOrders().toMutableList()
        orders.add(order)
        val ordersJson = gson.toJson(orders)
        jsonDataSource.saveToPreferences("user_orders", ordersJson)
    }

    /**
     * Obtiene todas las órdenes del usuario
     */
    fun getOrders(): List<Order> {
        val ordersJson = jsonDataSource.readFromPreferences("user_orders")
        return ordersJson?.let {
            val typeToken = object : TypeToken<List<Order>>() {}
            gson.fromJson<List<Order>>(it, typeToken.type)
        } ?: emptyList()
    }

    /**
     * Obtiene una orden específica por ID
     */
    fun getOrderById(orderId: String): Order? {
        return getOrders().firstOrNull { it.id == orderId }
    }

    // ============================================
    // FAVORITOS
    // ============================================

    /**
     * Guarda los items favoritos del usuario
     */
    fun saveFavorites(favoriteIds: List<String>) {
        val favoritesJson = gson.toJson(favoriteIds)
        jsonDataSource.saveToPreferences("favorites", favoritesJson)
    }

    /**
     * Obtiene los items favoritos
     */
    fun getFavorites(): List<String> {
        val favoritesJson = jsonDataSource.readFromPreferences("favorites")
        return favoritesJson?.let {
            val typeToken = object : TypeToken<List<String>>() {}
            gson.fromJson<List<String>>(it, typeToken.type)
        } ?: emptyList()
    }

    /**
     * Agrega un item a favoritos
     */
    fun addToFavorites(itemId: String) {
        val favorites = getFavorites().toMutableList()
        if (!favorites.contains(itemId)) {
            favorites.add(itemId)
            saveFavorites(favorites)
        }
    }

    /**
     * Elimina un item de favoritos
     */
    fun removeFromFavorites(itemId: String) {
        val favorites = getFavorites().toMutableList()
        favorites.remove(itemId)
        saveFavorites(favorites)
    }
}