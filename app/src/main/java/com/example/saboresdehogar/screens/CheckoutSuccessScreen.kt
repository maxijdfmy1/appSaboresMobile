
package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.viewmodel.OrderViewModel

@Composable
fun CheckoutSuccessScreen(navController: NavController, orderId: String, orderViewModel: OrderViewModel) {
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderById(orderId)
    }
    val order by orderViewModel.currentOrder.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color.Green,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Compra Exitosa!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (order != null) {
            Text(text = "Detalles del pedido:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "ID de la Orden: ${order!!.id}")
            Text(text = "Nombre: ${order!!.customerName}")
            Text(text = "Teléfono: ${order!!.customerPhone}")
            Text(text = "Tipo de Orden: ${order!!.orderType}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Items:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(order!!.items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${item.quantity}x ${item.menuItem.name}")
                        Text(text = "$${item.subtotal}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Total: $${order!!.total}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        } else {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.navigate("home") }) {
            Text("Seguir Comprando")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("home") }) {
            Text("Volver al Inicio")
        }
    }
}
