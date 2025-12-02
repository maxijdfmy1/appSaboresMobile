package com.example.saboresdehogar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saboresdehogar.data.repository.*
import com.example.saboresdehogar.data.source.local.JsonDataSource
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.data.source.remote.RetrofitClient

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Fuentes de datos
    private val apiService: ApiService = RetrofitClient.instance
    private val localDataSource: LocalDataSource = LocalDataSource(JsonDataSource(context))

    // Repositorios
    // CORRECCIÓN: Pasamos solo las dependencias que el constructor requiere.
    private val menuRepository = MenuRepository(apiService)
    private val userRepository = UserRepository(localDataSource, apiService)
    private val stockRepository = StockRepository(apiService)
    private val cartRepository = CartRepository(localDataSource)
    
    // CORRECCIÓN: AuthRepository ahora depende de ApiService y UserRepository.
    private val authRepository = AuthRepository(localDataSource, apiService, userRepository)
    private val orderRepository = OrderRepository(localDataSource, cartRepository, apiService)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> {
                MenuViewModel(menuRepository) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(cartRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                // CORRECCIÓN: Usamos la instancia correcta de AuthRepository.
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(orderRepository, cartRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(StockViewModel::class.java) -> {
                StockViewModel(stockRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
