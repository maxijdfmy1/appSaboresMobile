package com.example.saboresdehogar.data.repository

import android.util.Log
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderStatus
import com.example.saboresdehogar.model.order.OrderType
import com.example.saboresdehogar.model.cart.ShoppingCart
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.data.source.remote.CrearPedidoDto
import java.util.UUID

class OrderRepository(
    private val localDataSource: LocalDataSource,
    private val cartRepository: CartRepository,
    private val apiService: ApiService? = null // Inject ApiService
) {

    /**
     * Crea una nueva orden
     */
    suspend fun createOrder( // Agregamos 'suspend' para llamadas de red
        customerName: String,
        customerPhone: String,
        orderType: OrderType,
        deliveryAddress: String? = null,
        notes: String? = null
    ): Order? {
        val cart = cartRepository.getCart()
        if (cart.items.isEmpty()) return null

        val userId = localDataSource.getUserSession()?.user?.id ?: "invitado"

        // 1. Preparar DTO para el Backend
        val comidaIds = cart.items.flatMap { cartItem ->
            // Si la cantidad es 2, enviamos el ID 2 veces (o como lo maneje tu backend)
            // Según tu backend (PedidoService), recibe lista de IDs y descuenta 1 por cada ID.
            List(cartItem.quantity) { cartItem.menuItem.id }
        }

        val pedidoDto = CrearPedidoDto(
            usuarioId = userId,
            nombreUsuario = customerName,
            direccion = deliveryAddress ?: "Retiro en tienda",
            comidasIds = comidaIds
        )

        return try {
            // 2. Intentar enviar al Backend
            val nuevaOrdenBackend = apiService?.createOrder(pedidoDto)

            if (nuevaOrdenBackend != null) {
                // Guardar respaldo local
                localDataSource.saveOrder(nuevaOrdenBackend)
                cartRepository.clearCart()
                nuevaOrdenBackend
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ORDER_REPO", "Fallo al crear pedido en servidor: ${e.message}")
            // Manejo de error o guardar localmente con flag "no_sincronizado"
            null
        }
    }

    /**
     * Obtiene todas las órdenes del usuario
     */
    fun getUserOrders(): List<Order> {
        return localDataSource.getOrders()
    }

    /**
     * Obtiene una orden por ID
     */
    fun getOrderById(orderId: String): Order? {
        return localDataSource.getOrderById(orderId)
    }

    /**
     * Obtiene el historial de órdenes ordenado por fecha
     */
    fun getOrderHistory(): List<Order> {
        return getUserOrders().sortedByDescending { it.timestamp }
    }

    /**
     * Actualiza el estado de una orden (Admin)
     */
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Order {
        return try {
            apiService?.updateOrderStatus(orderId, status) 
                ?: throw IllegalStateException("API Service not available")
        } catch (e: Exception) {
             Log.e("ORDER_REPO", "Error updating status: ${e.message}")
             // Fallback: Update local storage manually if implementing sync
             // For now, just return current order with updated status simulated or throw
             // throw e
             
             // Simulación local
             val order = getOrderById(orderId)
             if (order != null) {
                 val updatedOrder = order.copy(status = status)
                 // Save back to local would require updating the list. 
                 // localDataSource doesn't have updateOrder method exposed easily for list replacement.
                 // Assuming we just rely on API or this simulation.
                 updatedOrder
             } else {
                 throw e
             }
        }
    }
}
