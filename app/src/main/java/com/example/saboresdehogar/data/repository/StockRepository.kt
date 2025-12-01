package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.model.stock.StockItem
import java.util.UUID

class StockRepository(private val apiService: ApiService?) {

    // Mock data for prototype if API is not ready
    private val mockStock = mutableListOf(
        StockItem(UUID.randomUUID().toString(), "Carne picada", 10, "kg"),
        StockItem(UUID.randomUUID().toString(), "Cebolla", 50, "unidades"),
        StockItem(UUID.randomUUID().toString(), "Papas", 100, "kg"),
        StockItem(UUID.randomUUID().toString(), "Arroz", 20, "kg"),
        StockItem(UUID.randomUUID().toString(), "Harina", 30, "kg")
    )

    suspend fun getStock(): List<StockItem> {
        return try {
            apiService?.getStock() ?: mockStock
        } catch (e: Exception) {
            mockStock
        }
    }

    suspend fun addStockItem(item: StockItem): StockItem {
        return try {
            apiService?.addStockItem(item) ?: run {
                mockStock.add(item)
                item
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateStockItem(item: StockItem): StockItem {
        return try {
            apiService?.updateStockItem(item.id, item) ?: run {
                val index = mockStock.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    mockStock[index] = item
                    item
                } else {
                    throw Exception("Item not found locally")
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
