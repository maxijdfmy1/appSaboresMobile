package com.example.saboresdehogar.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.saboresdehogar.data.repository.UserRepository
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserViewModelTest {

    // Regla para que LiveData funcione de forma síncrona en las pruebas
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userViewModel: UserViewModel
    private val userRepository: UserRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        // Usamos el dispatcher principal para las corrutinas de ViewModel
        Dispatchers.setMain(Dispatchers.Unconfined)
        userViewModel = UserViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllUsers should update allUsers LiveData`() = runBlocking {
        // Given
        val mockUsers = listOf(User("1", "test@test.com", "Test User", "123", role = UserRole.CUSTOMER))
        coEvery { userRepository.getAllUsers() } returns mockUsers

        // When
        userViewModel.loadAllUsers()

        // Then
        // Verificamos que el LiveData contiene los usuarios simulados
        assertEquals(mockUsers, userViewModel.allUsers.value)
    }

    @Test
    fun `deleteUser should call repository and reload users`() = runBlocking {
        // Given
        val userId = "1"
        coEvery { userRepository.deleteUser(userId) } returns Unit
        coEvery { userRepository.getAllUsers() } returns emptyList() // Simula la recarga

        // When
        userViewModel.deleteUser(userId)

        // Then
        // Verificamos que se llamó a deleteUser y luego a getAllUsers para recargar
        coVerify(ordering = io.mockk.Ordering.SEQUENCE) {
            userRepository.deleteUser(userId)
            userRepository.getAllUsers()
        }
    }

    @Test
    fun `updateUser should call repository and reload users`() = runBlocking {
        // Given
        val userId = "1"
        val userDto = ActualizarUsuarioDto("Updated User", "", "", "", "")
        val updatedUser = User("1", "", "Updated User", "", role = UserRole.CUSTOMER)
        coEvery { userRepository.updateUser(userId, userDto) } returns updatedUser
        coEvery { userRepository.getAllUsers() } returns listOf(updatedUser)

        // When
        userViewModel.updateUser(userId, userDto)

        // Then
        // Verificamos que se llamó a updateUser y luego a getAllUsers para recargar
        coVerify(ordering = io.mockk.Ordering.SEQUENCE) {
            userRepository.updateUser(userId, userDto)
            userRepository.getAllUsers()
        }
    }
}
