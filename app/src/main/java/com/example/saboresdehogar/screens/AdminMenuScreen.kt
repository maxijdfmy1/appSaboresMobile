package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.screens.components.AdminProductRow
import com.example.saboresdehogar.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    navController: NavController,
    menuViewModel: MenuViewModel
) {
    val menuItems by menuViewModel.menuItems.observeAsState(emptyList())
    val isLoading by menuViewModel.isLoading.observeAsState(false)
    val error by menuViewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        menuViewModel.loadMenu()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Menú") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { navController.navigate(Screen.AdminAddProduct.route) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Añadir Producto")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }
            else if (menuItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos en el menú.")
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(menuItems) { item ->
                        AdminProductRow(
                            item = item,
                            onEdit = { id ->
                                // Navegar a pantalla de edición (necesitamos crear la ruta)
                                navController.navigate("admin_edit_product/$id")
                            },
                            onDelete = { id ->
                                menuViewModel.deleteProduct(id) {
                                    // Mensaje opcional al borrar
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
