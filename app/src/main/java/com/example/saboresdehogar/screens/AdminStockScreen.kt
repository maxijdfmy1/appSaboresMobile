package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.model.stock.StockItem
import com.example.saboresdehogar.viewmodel.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStockScreen(
    navController: NavController,
    stockViewModel: StockViewModel
) {
    val stockItems by stockViewModel.stockItems.observeAsState(emptyList())
    val isLoading by stockViewModel.isLoading.observeAsState(false)
    val error by stockViewModel.error.observeAsState()

    // Estado para agregar nuevo item
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Almacén (Stock)") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, "Agregar Item")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text("Error: $error", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else if (stockItems.isEmpty()) {
                Text("El almacén está vacío.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(stockItems) { item ->
                        StockItemRow(item, stockViewModel)
                        Divider()
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddStockItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, qty, unit ->
                stockViewModel.addStockItem(name, qty, unit)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StockItemRow(item: StockItem, viewModel: StockViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text("${item.quantity} ${item.unit}", style = MaterialTheme.typography.bodyMedium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { 
                if (item.quantity > 0) viewModel.updateQuantity(item, item.quantity - 1) 
            }) {
                Icon(Icons.Default.Remove, "Reducir")
            }
            
            Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
            
            IconButton(onClick = { 
                viewModel.updateQuantity(item, item.quantity + 1) 
            }) {
                Icon(Icons.Default.Add, "Aumentar")
            }
        }
    }
}

@Composable
fun AddStockItemDialog(onDismiss: () -> Unit, onAdd: (String, Int, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var unit by remember { mutableStateOf("kg") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Insumo") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unidad (kg, gr, u)") })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotEmpty()) {
                    onAdd(name, quantity.toIntOrNull() ?: 0, unit)
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
