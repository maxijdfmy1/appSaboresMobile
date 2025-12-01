package com.example.saboresdehogar.util

import android.util.Patterns

object ValidationUtils {

    /**
     * Valida un RUT Chileno usando el algoritmo de Módulo 11.
     */
    fun isRutValid(rut: String): Boolean {
        var rutClean = rut.replace(".", "").replace("-", "").trim().uppercase()
        if (rutClean.length < 2) return false

        // Separar DV
        val dv = rutClean.last()
        val rutBody = rutClean.substring(0, rutClean.length - 1)

        if (!rutBody.all { it.isDigit() }) return false

        var rutNum = try {
            rutBody.toInt()
        } catch (e: NumberFormatException) {
            return false
        }

        var m = 0
        var s = 1
        while (rutNum != 0) {
            s = (s + rutNum % 10 * (9 - m++ % 6)) % 11
            rutNum /= 10
        }
        val expectedDv = if (s != 0) (s + 47).toChar() else 'K'

        return dv == expectedDv
    }

    /**
     * Valida un correo electrónico.
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida un número de teléfono chileno (9 dígitos).
     * Acepta formatos: +56912345678, 912345678, 12345678
     */
    fun isValidPhone(phone: String): Boolean {
        // Limpiar caracteres no numéricos excepto el + inicial
        val phoneClean = phone.trim()
        
        // Formato ideal: +569xxxxxxxx (12 dígitos)
        if (phoneClean.matches(Regex("^\\+569\\d{8}$"))) return true
        
        // Formato simple: 9xxxxxxxx (9 dígitos)
        if (phoneClean.matches(Regex("^9\\d{8}$"))) return true
        
        // Formato antiguo/fijo: 8 dígitos
        if (phoneClean.matches(Regex("^\\d{8}$"))) return true

        return false
    }
}
