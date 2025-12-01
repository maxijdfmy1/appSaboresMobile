package com.example.saboresdehogar.model.user

data class User(
    val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val photoUrl: String? = null,
    val role: UserRole = UserRole.CUSTOMER,
    val createdAt: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false,
    val addresses: List<Address> = emptyList(),
    val favoriteItems: List<String> = emptyList(),
    val rut: String? = null,
) {
    fun getDefaultAddress(): Address? {
        return addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
    }
}