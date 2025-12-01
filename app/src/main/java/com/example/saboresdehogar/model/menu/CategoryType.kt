package com.example.saboresdehogar.model.menu

enum class CategoryType {
    EMPANADAS_SOPAS,
    PLATOS_PRINCIPALES,
    COMPLETOS_SANDWICHES,
    ACOMPANAMIENTOS,
    BEBIDAS;

    fun getDisplayName(): String {
        return when (this) {
            EMPANADAS_SOPAS -> "Empanadas y Sopas"
            PLATOS_PRINCIPALES -> "Platos Principales"
            COMPLETOS_SANDWICHES -> "Completos y Sándwiches"
            ACOMPANAMIENTOS -> "Acompañamientos"
            BEBIDAS -> "Bebidas"
        }
    }
}