package com.example.saboresdehogar.data.source.remote

import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderStatus
import com.example.saboresdehogar.model.stock.StockItem
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // --- MENU ---
    @GET("menu")
    suspend fun getMenu(): List<MenuItem>

    @POST("menu")
    suspend fun addMenuItem(@Body menuItem: MenuItem): MenuItem

    @PUT("menu/{id}")
    suspend fun updateMenuItem(@Path("id") id: String, @Body menuItem: MenuItem): MenuItem

    @DELETE("menu/{id}")
    suspend fun deleteMenuItem(@Path("id") id: String): Response<Unit>

    // --- USERS ---
    @GET("users")
    suspend fun getUsers(): List<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: User): User

    // --- ORDERS ---
    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: String, @Body status: OrderStatus): Order

    // --- STOCK (ALMACÉN) ---
    @GET("stock")
    suspend fun getStock(): List<StockItem>

    @POST("stock")
    suspend fun addStockItem(@Body item: StockItem): StockItem

    @PUT("stock/{id}")
    suspend fun updateStockItem(@Path("id") id: String, @Body item: StockItem): StockItem
}
