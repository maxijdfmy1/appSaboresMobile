package com.example.saboresdehogar.data.repository

import android.util.Log
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.data.source.remote.CrearComidaDto
import com.example.saboresdehogar.data.source.remote.RecetaDto
import com.example.saboresdehogar.model.menu.MenuCategory
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuItem

class MenuRepository(
    private val apiService: ApiService
) {

    private var menuCache: List<MenuItem>? = null

    suspend fun getMenuItems(): List<MenuItem> {
        return try {
            val items = apiService.getMenu()
            menuCache = items
            Log.d("API_CHECK", "¡CONEXIÓN EXITOSA! Se cargaron ${items.size} items desde AWS.")
            items
        } catch (e: Exception) {
            Log.e("API_CHECK", "ERROR DE CONEXIÓN: ${e.message}. Usando datos de caché si existen.")
            menuCache ?: emptyList()
        }
    }

    suspend fun addMenuItem(item: MenuItem): MenuItem {
        return try {
            val dto = CrearComidaDto(
                nombre = item.name,
                precio = item.price,
                descripcion = item.description,
                imagenUrl = item.imageUrl ?: "",
                receta = emptyList() 
            )
            apiService.addMenuItem(dto)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al crear item: ${e.message}")
            throw e
        }
    }

    suspend fun updateMenuItem(item: MenuItem): MenuItem {
        return try {
            apiService.updateMenuItem(item.id, item)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al actualizar item: ${e.message}")
            throw e
        }
    }

    suspend fun deleteMenuItem(itemId: String) {
        try {
            apiService.deleteMenuItem(itemId)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al eliminar item: ${e.message}")
            throw e
        }
    }

    suspend fun getItemsByCategory(category: CategoryType): List<MenuItem> {
        val items = menuCache ?: getMenuItems()
        return items.filter { it.category == category }
    }

    suspend fun getMenuByCategories(): List<MenuCategory> {
        val items = menuCache ?: getMenuItems()
        return CategoryType.values().map { categoryType ->
            MenuCategory(
                type = categoryType,
                displayName = categoryType.getDisplayName(),
                items = items.filter { it.category == categoryType }
            )
        }.filter { it.items.isNotEmpty() }
    }

    suspend fun getItemById(itemId: String): MenuItem? {
        val items = menuCache ?: getMenuItems()
        return items.firstOrNull { it.id == itemId }
    }
    
    // CORRECCIÓN: Re-implementar funciones de filtrado.
    suspend fun searchItems(query: String): List<MenuItem> {
        val lowerQuery = query.lowercase()
        val items = menuCache ?: getMenuItems()
        return items.filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
        }
    }

    suspend fun getVegetarianItems(): List<MenuItem> {
        val items = menuCache ?: getMenuItems()
        return items.filter { it.isVegetarian }
    }

    suspend fun getAvailableItems(): List<MenuItem> {
        val items = menuCache ?: getMenuItems()
        return items.filter { it.isAvailable }
    }
    
    fun invalidateCache() {
        menuCache = null
    }
}
