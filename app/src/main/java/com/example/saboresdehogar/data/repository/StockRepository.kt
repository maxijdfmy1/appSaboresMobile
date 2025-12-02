package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.data.source.remote.CrearIngredienteDto
import com.example.saboresdehogar.model.stock.StockItem

class StockRepository(private val apiService: ApiService?) {

    suspend fun getStock(): List<StockItem> {
        return try {
            // CORRECCIÓN: Llamar a getIngredients() como se define en la ApiService actualizada.
            apiService?.getIngredients() ?: emptyList()
        } catch (e: Exception) {
            // En caso de error, devolver una lista vacía.
            emptyList()
        }
    }

    suspend fun addStockItem(item: StockItem): StockItem {
        return try {
            // CORRECCIÓN: Crear el DTO requerido por el endpoint `createIngredient`.
            val dto = CrearIngredienteDto(
                nombre = item.name,
                stock = item.quantity,
                unidad = item.unit
            )
            apiService?.createIngredient(dto) ?: throw IllegalStateException("Api service not available")
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateStockItem(item: StockItem): StockItem {
        return try {
            // CORRECCIÓN: Llamar al endpoint `updateIngredient`.
            // Nota: Este endpoint en el backend no modifica el stock, solo datos básicos.
            // Para el stock, se usaría `restockIngredient`.
            apiService?.updateIngredient(item.id) ?: throw IllegalStateException("Api service not available")
        } catch (e: Exception) {
            throw e
        }
    }
    
    suspend fun restockIngredient(itemId: String, amount: Int): StockItem {
        return try {
            apiService?.restockIngredient(itemId, amount) ?: throw IllegalStateException("Api service not available")
        } catch (e: Exception) {
            throw e
        }
    }
}
