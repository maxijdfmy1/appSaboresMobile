package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saboresdehogar.data.repository.CartRepository
import com.example.saboresdehogar.data.repository.OrderRepository
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderStatus
import com.example.saboresdehogar.model.order.OrderType
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?> = _order

    private val _currentOrder = MutableLiveData<Order?>()
    val currentOrder: LiveData<Order?> = _currentOrder

    private val _orderCreated = MutableLiveData<Boolean>()
    val orderCreated: LiveData<Boolean> = _orderCreated

    private val _orderError = MutableLiveData<String?>()
    val orderError: LiveData<String?> = _orderError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val orderResult = orderRepository.getOrderById(orderId)
                _order.postValue(orderResult)
            } catch (e: Exception) {
                _orderError.postValue("Error al obtener la orden: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }

    /**
     * Crea una nueva orden
     */
    fun createOrder(
        customerName: String,
        customerPhone: String,
        orderType: OrderType,
        deliveryAddress: String? = null,
        notes: String? = null
    ) {
        // Validaciones
        if (customerName.isBlank()) {
            _orderError.value = "El nombre es requerido"
            return
        }

        if (customerPhone.isBlank()) {
            _orderError.value = "El teléfono es requerido"
            return
        }

        if (orderType == OrderType.DELIVERY && deliveryAddress.isNullOrBlank()) {
            _orderError.value = "La dirección de entrega es requerida para delivery"
            return
        }

        if (cartRepository.getItemCount() == 0) {
            _orderError.value = "El carrito está vacío"
            return
        }

        _isLoading.value = true

        try {
            val order = orderRepository.createOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                orderType = orderType,
                deliveryAddress = deliveryAddress,
                notes = notes
            )

            if (order != null) {
                _currentOrder.value = order
                _orderCreated.value = true
                _orderError.value = null
                loadOrders() // Actualiza la lista
            } else {
                _orderError.value = "Error al crear la orden"
            }
        } catch (e: Exception) {
            _orderError.value = "Error: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Carga todas las órdenes del usuario
     */
    fun loadOrders() {
        _isLoading.value = true
        try {
            val orderList = orderRepository.getOrderHistory()
            _orders.value = orderList
        } catch (e: Exception) {
            _orderError.value = "Error al cargar órdenes: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Obtiene una orden específica
     */
    fun loadOrderById(orderId: String) {
        _isLoading.value = true
        try {
            val order = orderRepository.getOrderById(orderId)
            _currentOrder.value = order
            if (order == null) {
                _orderError.value = "Orden no encontrada"
            }
        } catch (e: Exception) {
            _orderError.value = "Error al cargar la orden: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Actualiza el estado de una orden (Admin)
     */
    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedOrder = orderRepository.updateOrderStatus(orderId, status)
                // Actualizamos la lista local si es necesario
                loadOrders() 
            } catch (e: Exception) {
                _orderError.value = "Error al actualizar estado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpia el estado de orden creada
     */
    fun clearOrderCreated() {
        _orderCreated.value = false
        _currentOrder.value = null
    }

    /**
     * Limpia errores
     */
    fun clearError() {
        _orderError.value = null
    }
}
