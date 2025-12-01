package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.saboresdehogar.model.cart.CartItem
import com.example.saboresdehogar.model.cart.ShoppingCart
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.data.repository.CartRepository

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    private val _cart = MutableLiveData<ShoppingCart>()
    val cart: LiveData<ShoppingCart> = _cart

    private val _cartTotal = MutableLiveData<Int>()
    val cartTotal: LiveData<Int> = _cartTotal

    private val _itemCount = MutableLiveData<Int>()
    val itemCount: LiveData<Int> = _itemCount

    private val _cartUpdated = MutableLiveData<Boolean>()
    val cartUpdated: LiveData<Boolean> = _cartUpdated

    init {
        loadCart()
    }

    /**
     * Carga el carrito actual
     */
    fun loadCart() {
        val currentCart = cartRepository.getCart()
        _cart.value = currentCart
        updateCartInfo()
    }

    /**
     * Agrega un item al carrito
     */
    fun addItem(item: MenuItem, quantity: Int = 1, notes: String? = null) {
        cartRepository.addItem(item, quantity, notes)
        loadCart()
        _cartUpdated.value = true
    }

    /**
     * Elimina un item del carrito
     */
    fun removeItem(itemId: String) {
        cartRepository.removeItem(itemId)
        loadCart()
        _cartUpdated.value = true
    }

    /**
     * Actualiza la cantidad de un item
     */
    fun updateQuantity(itemId: String, quantity: Int) {
        cartRepository.updateQuantity(itemId, quantity)
        loadCart()
        _cartUpdated.value = true
    }

    /**
     * Incrementa la cantidad de un item
     */
    fun incrementQuantity(itemId: String) {
        val cart = cartRepository.getCart()
        val item = cart.items.find { it.menuItem.id == itemId }
        item?.let {
            updateQuantity(itemId, it.quantity + 1)
        }
    }

    /**
     * Decrementa la cantidad de un item
     */
    fun decrementQuantity(itemId: String) {
        val cart = cartRepository.getCart()
        val item = cart.items.find { it.menuItem.id == itemId }
        item?.let {
            if (it.quantity > 1) {
                updateQuantity(itemId, it.quantity - 1)
            } else {
                removeItem(itemId)
            }
        }
    }

    /**
     * Limpia el carrito
     */
    fun clearCart() {
        cartRepository.clearCart()
        loadCart()
        _cartUpdated.value = true
    }

    /**
     * Actualiza información del carrito
     */
    private fun updateCartInfo() {
        _cartTotal.value = cartRepository.getCartTotal()
        _itemCount.value = cartRepository.getItemCount()
    }

    /**
     * Obtiene los items del carrito
     */
    fun getCartItems(): List<CartItem> {
        return _cart.value?.items ?: emptyList()
    }

    /**
     * Verifica si el carrito está vacío
     */
    fun isCartEmpty(): Boolean {
        return (_itemCount.value ?: 0) == 0
    }
}
