package com.example.saboresdehogar.data.repository

import android.util.Log
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.model.auth.AuthResponse
import com.example.saboresdehogar.model.auth.LoginCredentials
import com.example.saboresdehogar.model.auth.RegisterRequest
import com.example.saboresdehogar.model.auth.UserSession
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto
import com.example.saboresdehogar.model.user.LoginDto
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.model.user.Address
import com.example.saboresdehogar.model.user.UserRole
import java.util.UUID

class AuthRepository(
    private val localDataSource: LocalDataSource,
    private val apiService: ApiService,
    private val userRepository: UserRepository? = null
) {

    suspend fun login(credentials: LoginCredentials): AuthResponse {
        return try {
            val loginDto = LoginDto(email = credentials.email, password = credentials.password)
            val response = apiService.login(loginDto)

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                val user = User(
                    id = loginResponse.id,
                    email = loginResponse.email,
                    name = loginResponse.usuario,
                    role = UserRole.valueOf(loginResponse.rol),
                    phone = "",
                    rut = "",
                    addresses = emptyList()
                )

                val token = UUID.randomUUID().toString()
                val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)

                val session = UserSession(user = user, token = token, expiresAt = expiresAt)
                localDataSource.saveUserSession(session)

                AuthResponse(success = true, user = user, token = token, expiresAt = expiresAt, message = loginResponse.mensaje)
            } else {
                // --- MEJORA DE ERROR ---
                // Capturamos el código y el mensaje de error real del servidor.
                val errorBody = response.errorBody()?.string() ?: "Cuerpo del error vacío"
                val errorCode = response.code()
                val errorMessage = "Error del servidor (Código: $errorCode): $errorBody"
                Log.e("AuthRepository", errorMessage)
                AuthResponse(success = false, message = errorMessage, errorCode = "API_ERROR_$errorCode")
            }
        } catch (e: Exception) {
            // Esto captura errores de red (ej. IP incorrecta, sin internet)
            Log.e("AuthRepository", "Error de conexión: ${e.message}")
            AuthResponse(success = false, message = "Error de conexión: ${e.message}", errorCode = "NETWORK_ERROR")
        }
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        return try {
            val newUser = User(
                id = "",
                email = request.email,
                name = request.name,
                phone = request.phone,
                rut = request.rut,
                addresses = listOf(Address(id = "", street = request.address, number = "", comuna = "")),
                role = UserRole.CUSTOMER
            )
            val registeredUser = apiService.registerUser(newUser)
            login(LoginCredentials(request.email, request.password))
        } catch (e: Exception) {
            AuthResponse(success = false, message = "El email ya está registrado", errorCode = "EMAIL_EXISTS")
        }
    }

    suspend fun updateProfile(user: User): User {
        return try {
            val userDto = ActualizarUsuarioDto(
                nombre = user.name,
                rut = user.rut ?: "",
                email = user.email,
                telefono = user.phone,
                direccion = user.getDefaultAddress()?.getFullAddress() ?: ""
            )
            val updatedUser = userRepository?.updateUser(user.id, userDto)
                ?: throw IllegalStateException("UserRepository not available")

            val currentSession = localDataSource.getUserSession()
            if (currentSession != null) {
                val newSession = currentSession.copy(user = updatedUser)
                localDataSource.saveUserSession(newSession)
            }
            updatedUser
        } catch (e: Exception) {
            throw e
        }
    }

    fun getCurrentSession(): UserSession? {
        return localDataSource.getUserSession()
    }

    fun isLoggedIn(): Boolean {
        return localDataSource.isUserLoggedIn()
    }

    fun logout() {
        localDataSource.logout()
    }

    fun getCurrentUser(): User? {
        return getCurrentSession()?.user
    }
}
