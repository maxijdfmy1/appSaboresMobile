package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.model.menu.MenuItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MenuRepositoryTest {

    private lateinit var menuRepository: MenuRepository
    private val apiService: ApiService = mockk()

    @Before
    fun setUp() {
        menuRepository = MenuRepository(apiService)
    }

    @Test
    fun `getMenuItems success should return items from API`() = runBlocking {
        // Given
        val mockMenuItems = listOf(MenuItem(id = "1", name = "Plato 1", price = 10.0, description = "Desc 1"))
        coEvery { apiService.getMenu() } returns mockMenuItems

        // When
        val result = menuRepository.getMenuItems()

        // Then
        assertEquals(mockMenuItems, result)
        coVerify { apiService.getMenu() }
    }

    @Test
    fun `getMenuItems failure should return empty list`() = runBlocking {
        // Given
        coEvery { apiService.getMenu() } throws Exception("API failed")

        // When
        val result = menuRepository.getMenuItems()

        // Then
        assertEquals(emptyList<MenuItem>(), result)
    }

    @Test
    fun `addMenuItem success should call apiService`() = runBlocking {
        // Given
        val newItem = MenuItem(id = "2", name = "Plato 2", price = 15.0, description = "Desc 2")
        coEvery { apiService.addMenuItem(any()) } returns newItem

        // When
        menuRepository.addMenuItem(newItem)

        // Then
        coVerify { apiService.addMenuItem(any()) }
    }

    @Test
    fun `searchItems should filter correctly based on query`() = runBlocking {
        // Given
        val allItems = listOf(
            MenuItem(id = "1", name = "Sopa de Pollo", price = 8.0, description = "Caldo caliente"),
            MenuItem(id = "2", name = "Ensalada César", price = 12.0, description = "Lechuga y pollo")
        )
        menuRepository.getMenuItems() // Populate cache
        coEvery { apiService.getMenu() } returns allItems
        menuRepository.getMenuItems() // Call again to ensure cache is populated for the test

        // When
        val result = menuRepository.searchItems("pollo")

        // Then
        assertEquals(2, result.size)
        assertEquals("Sopa de Pollo", result[0].name)
    }
}
