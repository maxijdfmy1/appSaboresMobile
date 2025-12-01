package com.example.saboresdehogar.data.source.remote

import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderStatus
import com.example.saboresdehogar.model.stock.StockItem
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

// DTOs para las peticiones
data class CrearComidaDto(
    val nombre: String,
    val precio: Int,
    val descripcion: String,
    val imagenUrl: String,
    val receta: List<RecetaDto>
)

data class RecetaDto(
    @SerializedName("ingredienteId") val ingredienteId: String,
    @SerializedName("cantidadRequerida") val cantidadRequerida: Int
)

data class CrearIngredienteDto(
    val nombre: String,
    val stock: Int,
    val unidad: String
)

data class CrearPedidoDto(
    val usuarioId: String,
    val nombreUsuario: String,
    val direccion: String,
    val comidasIds: List<String>
)

interface ApiService {
    // --- AUTH ---
    @POST("api/auth/login")
    suspend fun login(@Body loginDto: com.example.saboresdehogar.model.user.LoginDto): Response<com.example.saboresdehogar.model.user.LoginResponse>

    // --- USUARIOS ---
    @GET("api/usuarios")
    suspend fun getUsers(): List<User>

    @GET("api/usuarios/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @POST("api/usuarios")
    suspend fun registerUser(@Body user: User): User

    @PUT("api/usuarios/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: ActualizarUsuarioDto): User

    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>

    // --- COMIDAS ---
    @GET("api/comidas")
    suspend fun getMenu(): List<MenuItem>

    @POST("api/comidas")
    suspend fun addMenuItem(@Body comida: CrearComidaDto): MenuItem

    @PUT("api/comidas/{id}")
    suspend fun updateMenuItem(@Path("id") id: String, @Body menuItem: MenuItem): MenuItem

    @DELETE("api/comidas/{id}")
    suspend fun deleteMenuItem(@Path("id") id: String): Response<Unit>

    // --- INGREDIENTES ---
    @GET("api/ingredientes")
    suspend fun getIngredients(): List<StockItem>

    @POST("api/ingredientes")
    suspend fun createIngredient(@Body ingrediente: CrearIngredienteDto): StockItem

    @PUT("api/ingredientes/{id}")
    suspend fun updateIngredient(@Path("id") id: String): StockItem

    @PUT("api/ingredientes/{id}/stock")
    suspend fun restockIngredient(@Path("id") id: String, @Query("cantidad") cantidad: Int): StockItem

    @DELETE("api/ingredientes/{id}")
    suspend fun deleteIngredient(@Path("id") id: String): Response<Unit>

    // --- PEDIDOS ---
    @GET("api/pedidos")
    suspend fun getOrders(): List<Order>

    @POST("api/pedidos")
    suspend fun createOrder(@Body pedido: CrearPedidoDto): Order

    @PUT("api/pedidos/{id}/estado")
    suspend fun updateOrderStatus(@Path("id") id: String, @Query("nuevoEstado") nuevoEstado: OrderStatus): Order

    @DELETE("api/pedidos/{id}")
    suspend fun deleteOrder(@Path("id") id: String): Response<Unit>
}
