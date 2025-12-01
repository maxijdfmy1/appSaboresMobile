package com.example.saboresdehogar.data.source.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException


class JsonDataSource(private val context: Context) {

    private val gson = Gson()

    /**
     * Lee un archivo JSON desde la carpeta assets
     */
    fun <T> readJsonFromAssets(fileName: String, typeToken: TypeToken<T>): T? {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            return gson.fromJson<T>(jsonString, typeToken.type)
        } catch (e: IOException) {
            // En lugar de ocultar el error, lo lanzamos para que se pueda diagnosticar.
            // Un error de IO (como archivo no encontrado) es crítico aquí.
            throw IOException("Error al leer el archivo '$fileName' desde assets. ¿Estás seguro de que el archivo existe?", e)
        } catch (e: Exception) { // Captura JsonSyntaxException, etc.
            // Un error de parseo también es crítico.
            throw RuntimeException("Error al parsear el JSON del archivo '$fileName'. Revisa si el formato es correcto.", e)
        }
    }

    /**
     * Lee un archivo JSON desde res/raw
     */
    fun <T> readJsonFromRaw(resourceId: Int, typeToken: TypeToken<T>): T? {
        return try {
            val jsonString = context.resources.openRawResource(resourceId)
                .bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, typeToken.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Guarda datos en SharedPreferences (para carrito, sesión, etc.)
     */
    fun saveToPreferences(key: String, data: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString(key, data).apply()
    }

    /**
     * Lee datos de SharedPreferences
     */
    fun readFromPreferences(key: String): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    /**
     * Elimina datos de SharedPreferences
     */
    fun removeFromPreferences(key: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().remove(key).apply()
    }

    /**
     * Limpia todas las preferencias
     */
    fun clearPreferences() {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}