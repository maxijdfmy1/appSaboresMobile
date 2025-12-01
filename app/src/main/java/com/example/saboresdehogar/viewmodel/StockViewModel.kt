package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saboresdehogar.data.repository.StockRepository
import com.example.saboresdehogar.model.stock.StockItem
import kotlinx.coroutines.launch

class StockViewModel(private val stockRepository: StockRepository) : ViewModel() {

    private val _stockItems = MutableLiveData<List<StockItem>>()
    val stockItems: LiveData<List<StockItem>> = _stockItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadStock()
    }

    fun loadStock() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _stockItems.value = stockRepository.getStock()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Error al cargar stock: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(item: StockItem, newQuantity: Int) {
        viewModelScope.launch {
            try {
                val updatedItem = item.copy(quantity = newQuantity)
                stockRepository.updateStockItem(updatedItem)
                loadStock() // Refresh list
            } catch (e: Exception) {
                _error.value = "Error al actualizar: ${e.message}"
            }
        }
    }
    
    fun addStockItem(name: String, quantity: Int, unit: String) {
        viewModelScope.launch {
            try {
                val newItem = StockItem(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    quantity = quantity,
                    unit = unit
                )
                stockRepository.addStockItem(newItem)
                loadStock()
            } catch (e: Exception) {
                _error.value = "Error al agregar: ${e.message}"
            }
        }
    }
}
