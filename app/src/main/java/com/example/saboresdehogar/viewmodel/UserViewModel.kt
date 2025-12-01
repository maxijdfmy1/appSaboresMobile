package com.example.saboresdehogar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.repository.UserRepository
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _favorites = MutableLiveData<List<String>>()
    val favorites: LiveData<List<String>> = _favorites

    // Admin: Lista de todos los usuarios
    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers: LiveData<List<User>> = _allUsers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCurrentUser()
        loadFavorites()
    }

    /**
     * Carga el usuario actual
     */
    fun loadCurrentUser() {
        _currentUser.value = userRepository.getCurrentUser()
    }

    /**
     * Carga los favoritos del usuario
     */
    fun loadFavorites() {
        _favorites.value = userRepository.getFavorites()
    }

    /**
     * Carga todos los usuarios (Admin)
     */
    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _allUsers.value = userRepository.getAllUsers()
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Elimina un usuario (Admin)
     */
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                loadAllUsers() // Recargar la lista
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    /**
     * Actualiza un usuario (Admin)
     */
    fun updateUser(userId: String, userDto: ActualizarUsuarioDto) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(userId, userDto)
                loadAllUsers() // Recargar la lista
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    /**
     * Agrega un item a favoritos
     */
    fun addToFavorites(itemId: String) {
        userRepository.addToFavorites(itemId)
        loadFavorites()
    }

    /**
     * Elimina un item de favoritos
     */
    fun removeFromFavorites(itemId: String) {
        userRepository.removeFromFavorites(itemId)
        loadFavorites()
    }

    /**
     * Alterna el estado de favorito
     */
    fun toggleFavorite(itemId: String) {
        userRepository.toggleFavorite(itemId)
        loadFavorites()
    }

    /**
     * Verifica si un item es favorito
     */
    fun isFavorite(itemId: String): Boolean {
        return userRepository.isFavorite(itemId)
    }
}
