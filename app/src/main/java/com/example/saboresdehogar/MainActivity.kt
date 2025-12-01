package com.example.saboresdehogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.saboresdehogar.components.SaboresApp // <-- IMPORTAMOS NUESTRA APP
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            SaboresDeHogarTheme {
                // --- ¡ESTE ES EL CAMBIO! ---
                // Reemplazamos el Scaffold y Greeting por esto:
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SaboresApp() // <-- ¡Llamamos a nuestra app completa!
                }
            }
        }
    }
}

// Ya no necesitamos las funciones Greeting() ni GreetingPreview()