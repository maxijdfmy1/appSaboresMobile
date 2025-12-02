package com.example.saboresdehogar.data.repository

import android.util.Log
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto

class UserRepository(
    private val localDataSource: LocalDataSource,
    private val apiService: ApiService? = null
) {

    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser(): User? {
        return localDataSource.getUserSession()?.user
    }

    /**
     * Obtiene todos los usuarios (Admin)
     */
    suspend fun getAllUsers(): List<User> {
        return try {
             apiService?.getUsers() ?: localDataSource.getUsers() ?: emptyList()
        } catch (e: Exception) {
            Log.e("USER_REPO", "Error fetching users: ${e.message}")
            localDataSource.getUsers() ?: emptyList()
        }
    }

    /**
     * Actualiza el perfil de un usuario
     */
    /**
     * Versión 1: Para el Admin (recibe ID y DTO directamente desde el diálogo de edición)
     * Soluciona el error en UserViewModel.kt
     */
    suspend fun updateUser(id: String, userDto: ActualizarUsuarioDto): User {
        return try {
            apiService?.updateUser(id, userDto) ?: throw IllegalStateException("API not available")
        } catch (e: Exception) {
            Log.e("USER_REPO", "Error updating user: ${e.message}")
            throw e
        }
    }

    /**
     * Versión 2: Para AuthRepository/Perfil (recibe el objeto User completo)
     * Mantiene la compatibilidad con AuthRepository.kt
     */
    suspend fun updateUser(user: User): User {
        // Construimos el DTO internamente
        val rutSeguro = user.rut ?: ""
        val direccionSegura = user.getDefaultAddress()?.street ?: ""

        val dto = ActualizarUsuarioDto(
            nombre = user.name,
            rut = rutSeguro,
            email = user.email,
            telefono = user.phone,
            direccion = direccionSegura
        )

        // Llamamos a la versión 1 reutilizando la lógica
        return updateUser(user.id, dto)
    }

    /**
     * Elimina un usuario
     */
    suspend fun deleteUser(id: String) {
        try {
            apiService?.deleteUser(id) ?: throw IllegalStateException("API not available")
        } catch (e: Exception) {
            Log.e("USER_REPO", "Error deleting user: ${e.message}")
            throw e
        }
    }

    /**
     * Obtiene los items favoritos del usuario
     */
    fun getFavorites(): List<String> {
        return localDataSource.getFavorites()
    }

    /**
     * Agrega un item a favoritos
     */
    fun addToFavorites(itemId: String) {
        localDataSource.addToFavorites(itemId)
    }

    /**
     * Elimina un item de favoritos
     */
    fun removeFromFavorites(itemId: String) {
        localDataSource.removeFromFavorites(itemId)
    }

    /**
     * Verifica si un item es favorito
     */
    fun isFavorite(itemId: String): Boolean {
        return getFavorites().contains(itemId)
    }

    /**
     * Alterna el estado de favorito de un item
     */
    fun toggleFavorite(itemId: String) {
        if (isFavorite(itemId)) {
            removeFromFavorites(itemId)
        } else {
            addToFavorites(itemId)
        }
    }
}
