package com.example.saboresdehogar.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saboresdehogar.data.repository.*
import com.example.saboresdehogar.data.source.local.JsonDataSource
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.RetrofitClient

/**
 * Factory para crear ViewModels con dependencias
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Inicializar data sources
    private val jsonDataSource = JsonDataSource(context)
    private val localDataSource = LocalDataSource(jsonDataSource)
    private val apiService = RetrofitClient.instance

    // Inicializar repositories
    private val menuRepository = MenuRepository(localDataSource, apiService)
    private val authRepository = AuthRepository(localDataSource, userRepository = null) // Initialize properly later or use lazy
    private val cartRepository = CartRepository(localDataSource)
    private val orderRepository = OrderRepository(localDataSource, cartRepository, apiService)
    private val userRepository = UserRepository(localDataSource, apiService)
    private val stockRepository = StockRepository(apiService)

    // Fix circular dependency if any, or just re-init AuthRepository with UserRepo if needed
    // For now AuthRepository doesn't strictly need UserRepo in constructor if passed in method, but I added it to constructor previously.
    // Let's fix AuthRepository initialization to include UserRepository
    private val authRepositoryWithUser = AuthRepository(localDataSource, userRepository)

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
                AuthViewModel(authRepositoryWithUser) as T
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
