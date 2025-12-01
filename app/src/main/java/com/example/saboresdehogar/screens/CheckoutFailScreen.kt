package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme

@Composable
fun CheckoutFailScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Error en la Compra!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No pudimos procesar tu pedido. (Simulación de error de pago o falta de stock).",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Botón para revisar el carrito
        Button(
            onClick = {
                // Simplemente vuelve a la pantalla anterior (el carrito)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Revisar Carrito", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver al inicio
        TextButton(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    // Limpiamos la pila de navegación
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            }
        ) {
            Text("Volver al Inicio")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutFailScreenPreview() {
    SaboresDeHogarTheme {
        CheckoutFailScreen(navController = rememberNavController())
    }
}