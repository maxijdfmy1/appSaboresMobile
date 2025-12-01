package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuCategory
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.data.repository.MenuRepository
import kotlinx.coroutines.launch

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    // LiveData para observar desde la UI
    private val _menuItems = MutableLiveData<List<MenuItem>>()
    val menuItems: LiveData<List<MenuItem>> = _menuItems

    private val _menuCategories = MutableLiveData<List<MenuCategory>>()
    val menuCategories: LiveData<List<MenuCategory>> = _menuCategories

    private val _selectedCategory = MutableLiveData<CategoryType?>()
    val selectedCategory: LiveData<CategoryType?> = _selectedCategory

    private val _searchResults = MutableLiveData<List<MenuItem>>()
    val searchResults: LiveData<List<MenuItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadMenu()
    }

    /**
     * Carga todos los items del menú
     */
    fun loadMenu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = menuRepository.getMenuItems()
                _menuItems.value = items
                _menuCategories.value = menuRepository.getMenuByCategories()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar el menú: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Agrega un producto (Admin)
     */
    fun addProduct(item: MenuItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                menuRepository.addMenuItem(item)
                loadMenu() // Recargar menú
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Error al agregar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza un producto (Admin)
     */
    fun updateProduct(item: MenuItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                menuRepository.updateMenuItem(item)
                loadMenu() // Recargar menú
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Error al actualizar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un producto (Admin)
     */
    fun deleteProduct(itemId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                menuRepository.deleteMenuItem(itemId)
                loadMenu() // Recargar menú
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Error al eliminar producto: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filtra por categoría
     */
    fun filterByCategory(category: CategoryType?) {
        viewModelScope.launch {
            _selectedCategory.value = category
            _menuItems.value = if (category != null) {
                menuRepository.getItemsByCategory(category)
            } else {
                menuRepository.getMenuItems()
            }
        }
    }

    /**
     * Busca items por nombre o descripción
     */
    fun searchItems(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }
            _searchResults.value = menuRepository.searchItems(query)
        }
    }

    /**
     * Limpia la búsqueda
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
    }

    suspend fun getItemById(itemId: String): MenuItem? {
        return menuRepository.getItemById(itemId)
    }

    /**
     * Obtiene items vegetarianos
     */
    fun loadVegetarianItems() {
        viewModelScope.launch {
            _menuItems.value = menuRepository.getVegetarianItems()
        }
    }

    /**
     * Obtiene items disponibles
     */
    fun loadAvailableItems() {
        viewModelScope.launch {
            _menuItems.value = menuRepository.getAvailableItems()
        }
    }
}
