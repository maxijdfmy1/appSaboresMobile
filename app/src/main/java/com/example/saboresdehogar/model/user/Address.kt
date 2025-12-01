package com.example.saboresdehogar.model.user

// Dejamos esta clase preparada por si la usamos en el perfil
// Pero para el registro usaremos un campo simple en User.
data class Address(
    val id: String,
    val street: String,
    val number: String,
    val apartment: String? = null,
    val comuna: String,
    val city: String = "Santiago",
    val reference: String? = null,
    val isDefault: Boolean = false
) {
    fun getFullAddress(): String {
        val apt = apartment?.let { ", Depto. $it" } ?: ""
        return "$street $number$apt, $comuna, $city"
    }
}