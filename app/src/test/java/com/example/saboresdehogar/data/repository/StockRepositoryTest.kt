package com.example.saboresdehogar.data.repository

import com.example.saboresdehogar.data.source.remote.ApiService
import com.example.saboresdehogar.data.source.remote.CrearIngredienteDto
import com.example.saboresdehogar.model.stock.StockItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StockRepositoryTest {

    private lateinit var stockRepository: StockRepository
    private val apiService: ApiService = mockk()

    @Before
    fun setUp() {
        stockRepository = StockRepository(apiService)
    }

    @Test
    fun `getStock success should return items from API`() = runBlocking {
        // Given
        val mockStockItems = listOf(StockItem("1", "Tomate", 10, "kg"))
        coEvery { apiService.getIngredients() } returns mockStockItems

        // When
        val result = stockRepository.getStock()

        // Then
        assertEquals(mockStockItems, result)
        coVerify { apiService.getIngredients() }
    }

    @Test
    fun `getStock failure should return empty list`() = runBlocking {
        // Given
        coEvery { apiService.getIngredients() } throws Exception("API Error")

        // When
        val result = stockRepository.getStock()

        // Then
        assertEquals(emptyList<StockItem>(), result)
    }

    @Test
    fun `addStockItem success should call apiService with correct DTO`() = runBlocking {
        // Given
        val stockItem = StockItem("new-id", "Cebolla", 5, "unidades")
        val expectedDto = CrearIngredienteDto(
            nombre = stockItem.name,
            stock = stockItem.quantity,
            unidad = stockItem.unit
        )
        coEvery { apiService.createIngredient(any()) } returns stockItem

        // When
        stockRepository.addStockItem(stockItem)

        // Then
        coVerify { apiService.createIngredient(expectedDto) }
    }

    @Test
    fun `restockIngredient success should call apiService`() = runBlocking {
        // Given
        val itemId = "1"
        val amount = 10
        val mockStockItem = StockItem(itemId, "Tomate", 20, "kg")
        coEvery { apiService.restockIngredient(itemId, amount) } returns mockStockItem

        // When
        stockRepository.restockIngredient(itemId, amount)

        // Then
        coVerify { apiService.restockIngredient(itemId, amount) }
    }
}
