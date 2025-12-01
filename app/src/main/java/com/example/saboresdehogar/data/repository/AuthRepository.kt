package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.model.auth.AuthResponse
import com.example.saboresdehogar.model.auth.LoginCredentials
import com.example.saboresdehogar.model.auth.RegisterRequest
import com.example.saboresdehogar.model.auth.UserSession
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.model.user.Address
import com.example.saboresdehogar.model.user.UserRole // <-- IMPORTANTE
import java.util.UUID

class AuthRepository(
    private val localDataSource: LocalDataSource,
    private val userRepository: UserRepository? = null // Inject UserRepository
) {

    /**
     * Login de usuario (MODIFICADO PARA ADMIN)
     */
    fun login(credentials: LoginCredentials): AuthResponse {
        var user = localDataSource.getUserByEmail(credentials.email)

        return if (user != null) {

            // --- INICIO DE LÓGICA DE ADMIN (SIMULACIÓN) ---
            // Si el email es admin@sabores.cl, forzamos el rol de ADMIN
            if (user.email.equals("admin@sabores.cl", ignoreCase = true)) {
                val adminAddress = Address(
                    id = "admin-addr-001",
                    street = "Oficina Central",
                    number = "123",
                    comuna = "Santiago",
                    isDefault = true
                )
                user = user.copy(role = UserRole.ADMIN, addresses = listOf(adminAddress))
            } else {
                // Asegurarnos que cualquier otro sea CUSTOMER
                user = user.copy(role = UserRole.CUSTOMER)
            }
            // --- FIN DE LÓGICA DE ADMIN ---

            val token = UUID.randomUUID().toString()
            val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 horas

            val session = UserSession(
                user = user,
                token = token,
                expiresAt = expiresAt
            )

            localDataSource.saveUserSession(session)

            AuthResponse(
                success = true,
                user = user,
                token = token,
                expiresAt = expiresAt,
                message = "Login exitoso"
            )
        } else {
            AuthResponse(
                success = false,
                message = "Usuario o contraseña incorrectos",
                errorCode = "INVALID_CREDENTIALS"
            )
        }
    }

    /**
     * Registro de nuevo usuario (CORREGIDO)
     */
    fun register(request: RegisterRequest): AuthResponse {
        // Verificar si el usuario ya existe
        val existingUser = localDataSource.getUserByEmail(request.email)

        return if (existingUser != null) {
            AuthResponse(
                success = false,
                message = "El email ya está registrado",
                errorCode = "EMAIL_EXISTS"
            )
        } else {
            // Crear nuevo usuario (con los campos nuevos)
            val newUser = User(
                id = UUID.randomUUID().toString(),
                email = request.email,
                name = request.name,
                phone = request.phone,
                rut = request.rut,
                addresses = listOf(Address(id = UUID.randomUUID().toString(), street = request.address, number = "", comuna = "", isDefault = true)),
                role = UserRole.CUSTOMER // Todos los registros son CUSTOMER
            )

            // En una app real, aquí guardarías el usuario en la base de datos
            // Por ahora solo creamos la sesión
            val token = UUID.randomUUID().toString()
            val expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)

            val session = UserSession(
                user = newUser,
                token = token,
                expiresAt = expiresAt
            )

            localDataSource.saveUserSession(session)

            AuthResponse(
                success = true,
                user = newUser,
                token = token,
                expiresAt = expiresAt,
                message = "Registro exitoso"
            )
        }
    }

    suspend fun updateProfile(user: User): User {
        val updatedUser = userRepository?.updateUser(user) ?: throw IllegalStateException("UserRepository not available")
        // Update session
        val currentSession = localDataSource.getUserSession()
        if(currentSession != null) {
            val newSession = currentSession.copy(user = updatedUser)
            localDataSource.saveUserSession(newSession)
        }
        return updatedUser
    }

    /**
     * Obtiene la sesión actual
     */
    fun getCurrentSession(): UserSession? {
        return localDataSource.getUserSession()
    }

    /**
     * Verifica si hay usuario logueado
     */
    fun isLoggedIn(): Boolean {
        return localDataSource.isUserLoggedIn()
    }

    /**
     * Cierra sesión
     */
    fun logout() {
        localDataSource.logout()
    }

    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser(): User? {
        // Modificado para reflejar el rol de la sesión
        return getCurrentSession()?.user
    }
}