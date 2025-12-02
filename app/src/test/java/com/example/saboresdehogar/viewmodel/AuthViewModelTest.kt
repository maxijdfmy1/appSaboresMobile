package com.example.saboresdehogar.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.saboresdehogar.data.repository.AuthRepository
import com.example.saboresdehogar.model.auth.AuthResponse
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var authViewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        authViewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success updates authState to Authenticated`() {
        // Given
        val successResponse = AuthResponse(
            success = true,
            user = User("1", "test@test.com", "Test", "", role = UserRole.CUSTOMER),
            token = "token",
            expiresAt = 123L,
            message = "Success"
        )
        coEvery { authRepository.login(any()) } returns successResponse

        // When
        authViewModel.login("test@test.com", "password")

        // Then
        assertTrue(authViewModel.authState.value is AuthState.Authenticated)
    }

    @Test
    fun `login failure updates authState to Error`() {
        // Given
        val errorResponse = AuthResponse(success = false, message = "Error")
        coEvery { authRepository.login(any()) } returns errorResponse

        // When
        authViewModel.login("wrong@test.com", "wrong")

        // Then
        assertTrue(authViewModel.authState.value is AuthState.Error)
    }

    @Test
    fun `logout should call repository and update state to Unauthenticated`() {
        // When
        authViewModel.logout()

        // Then
        coVerify { authRepository.logout() }
        assertTrue(authViewModel.authState.value is AuthState.Unauthenticated)
    }
}
