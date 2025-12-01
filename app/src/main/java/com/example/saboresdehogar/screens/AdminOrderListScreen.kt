package com.example.saboresdehogar.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.model.order.Order
import com.example.saboresdehogar.model.order.OrderStatus
import com.example.saboresdehogar.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderListScreen(
    navController: NavController,
    orderViewModel: OrderViewModel
) {
    val orders by orderViewModel.orders.observeAsState(emptyList())
    val isLoading by orderViewModel.isLoading.observeAsState(false)
    val error by orderViewModel.orderError.observeAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Pedidos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (orders.isEmpty()) {
                Text(
                    text = "No hay pedidos activos.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        AdminOrderRow(
                            order = order,
                            onStatusChange = { newStatus ->
                                orderViewModel.updateOrderStatus(order.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderRow(
    order: Order,
    onStatusChange: (OrderStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orden #${order.id.take(8)}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = dateFormat.format(Date(order.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cliente: ${order.customerName} (${order.customerPhone})")
            Text("Total: $${String.format("%,d", order.total)}")
            if (order.deliveryAddress != null) {
                Text("Dirección: ${order.deliveryAddress}")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selector de Estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado: ", fontWeight = FontWeight.SemiBold)
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(order.status.getDisplayName()) // <-- USO DE ESPAÑOL
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        OrderStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.getDisplayName()) }, // <-- USO DE ESPAÑOL
                                onClick = {
                                    onStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
