package com.example.saboresdehogar.data.repository

import android.util.Log
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.model.menu.MenuCategory
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.ApiService
import java.util.UUID

class MenuRepository(
    private val localDataSource: LocalDataSource,
    private val apiService: ApiService
) {

    /**
     * Obtiene todos los items del menú
     */
    suspend fun getMenuItems(): List<MenuItem> {
        return try {
            // Intentamos obtener los datos de la API
            val items = apiService.getMenu()
            Log.d("API_CHECK", "¡CONEXIÓN EXITOSA! Se cargaron ${items.size} items desde AWS.")
            items
        } catch (e: Exception) {
            // Si falla, usamos los datos locales como fallback
            Log.e("API_CHECK", "ERROR DE CONEXIÓN: ${e.message}. Usando datos locales.")
            e.printStackTrace()
            localDataSource.getMenuItems() ?: emptyList()
        }
    }

    /**
     * Agrega un nuevo item al menú
     */
    suspend fun addMenuItem(item: MenuItem): MenuItem {
        return try {
             apiService.addMenuItem(item)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al crear item: ${e.message}")
            throw e
        }
    }

    /**
     * Actualiza un item del menú
     */
    suspend fun updateMenuItem(item: MenuItem): MenuItem {
        return try {
            apiService.updateMenuItem(item.id, item)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al actualizar item: ${e.message}")
            throw e
        }
    }

    /**
     * Elimina un item del menú
     */
    suspend fun deleteMenuItem(itemId: String) {
        try {
            apiService.deleteMenuItem(itemId)
        } catch (e: Exception) {
            Log.e("API_CRUD", "Error al eliminar item: ${e.message}")
            throw e
        }
    }

    /**
     * Obtiene items por categoría
     */
    suspend fun getItemsByCategory(category: CategoryType): List<MenuItem> {
        return getMenuItems().filter { it.category == category }
    }

    /**
     * Obtiene el menú agrupado por categorías
     */
    suspend fun getMenuByCategories(): List<MenuCategory> {
        val items = getMenuItems()
        return CategoryType.values().map { categoryType ->
            MenuCategory(
                type = categoryType,
                displayName = categoryType.getDisplayName(),
                items = items.filter { it.category == categoryType }
            )
        }.filter { it.items.isNotEmpty() }
    }

    /**
     * Busca un item por ID
     */
    suspend fun getItemById(itemId: String): MenuItem? {
        return getMenuItems().firstOrNull { it.id == itemId }
    }

    /**
     * Busca items por nombre (búsqueda)
     */
    suspend fun searchItems(query: String): List<MenuItem> {
        val lowerQuery = query.lowercase()
        return getMenuItems().filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.description.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Obtiene items vegetarianos
     */
    suspend fun getVegetarianItems(): List<MenuItem> {
        return getMenuItems().filter { it.isVegetarian }
    }

    /**
     * Obtiene items disponibles
     */
    suspend fun getAvailableItems(): List<MenuItem> {
        return getMenuItems().filter { it.isAvailable }
    }
}
