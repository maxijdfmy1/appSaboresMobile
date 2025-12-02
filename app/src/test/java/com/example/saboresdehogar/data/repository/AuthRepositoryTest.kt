package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.data.source.local.LocalDataSource
import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.model.auth.LoginCredentials
import com.example.saboresdehogar.model.auth.RegisterRequest
import com.example.saboresdehogar.model.user.LoginResponse
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {

    private lateinit var authRepository: AuthRepository
    private val localDataSource: LocalDataSource = mockk(relaxed = true)
    private val apiService: ApiService = mockk()

    @Before
    fun setUp() {
        // Instanciamos el repositorio con los mocks
        authRepository = AuthRepository(localDataSource, apiService, mockk(relaxed = true))
    }

    @Test
    fun `login success should save session and return success`() = runBlocking {
        // Given
        val credentials = LoginCredentials("test@example.com", "password")
        val loginResponse = LoginResponse(
            mensaje = "Login exitoso",
            usuario = "Test User",
            email = "test@example.com",
            rol = UserRole.CUSTOMER.name,
            id = "123"
        )
        // Simulamos una respuesta exitosa del API
        coEvery { apiService.login(any()) } returns Response.success(loginResponse)

        // When
        val result = authRepository.login(credentials)

        // Then
        assertTrue(result.success)
        // Verificamos que se haya guardado la sesión
        coVerify { localDataSource.saveUserSession(any()) }
    }

    @Test
    fun `login failure should not save session and return error`() = runBlocking {
        // Given
        val credentials = LoginCredentials("wrong@example.com", "wrong_password")
        // Simulamos una respuesta de error del API (ej. 401 No autorizado)
        coEvery { apiService.login(any()) } returns Response.error(401, mockk(relaxed = true))

        // When
        val result = authRepository.login(credentials)

        // Then
        assertFalse(result.success)
        // Verificamos que NO se haya intentado guardar la sesión
        coVerify(exactly = 0) { localDataSource.saveUserSession(any()) }
    }

    @Test
    fun `network error during login should return network error`() = runBlocking {
        // Given
        val credentials = LoginCredentials("test@example.com", "password")
        // Simulamos una excepción de red
        coEvery { apiService.login(any()) } throws Exception("Network failed")

        // When
        val result = authRepository.login(credentials)

        // Then
        assertFalse(result.success)
        assertEquals("NETWORK_ERROR", result.errorCode)
    }

    @Test
    fun `register success should call login`() = runBlocking {
        // Given
        val registerRequest = RegisterRequest("new@example.com", "password", "New User", "12345", "1-9", "Some Address")
        val registeredUser = User(id = "456", name = "New User", email = "new@example.com", phone = "12345", role = UserRole.CUSTOMER)
        // Simulamos un registro exitoso
        coEvery { apiService.registerUser(any()) } returns registeredUser
        // Y también un login exitoso después del registro
        coEvery { apiService.login(any()) } returns Response.success(LoginResponse("Login exitoso", "New User", "new@example.com", UserRole.CUSTOMER.name, "456"))

        // When
        authRepository.register(registerRequest)

        // Then
        // Verificamos que después de registrar, se intente hacer login (para el auto-login)
        coVerify { apiService.login(any()) }
    }

    @Test
    fun `register failure with existing email returns error`() = runBlocking {
        // Given
        val registerRequest = RegisterRequest("existing@example.com", "password", "User", "", "", "")
        // Simulamos que el registro falla con una excepción (ej. email ya existe)
        coEvery { apiService.registerUser(any()) } throws Exception("Email already exists")

        // When
        val result = authRepository.register(registerRequest)

        // Then
        assertFalse(result.success)
        assertEquals("EMAIL_EXISTS", result.errorCode)
        // Verificamos que no se intentó hacer login si el registro falló
        coVerify(exactly = 0) { apiService.login(any()) }
    }
}
