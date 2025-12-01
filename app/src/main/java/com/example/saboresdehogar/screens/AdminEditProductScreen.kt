package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditProductScreen(
    navController: NavController,
    menuViewModel: MenuViewModel,
    productId: String
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(CategoryType.PLATOS_PRINCIPALES) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar datos
    LaunchedEffect(productId) {
        val item = menuViewModel.getItemById(productId)
        if (item != null) {
            name = item.name
            description = item.description
            price = item.price.toString()
            imageUrl = item.imageUrl ?: ""
            ingredients = item.ingredients.joinToString(", ")
            category = item.category
            isLoading = false
        } else {
            // Manejar error si no encuentra el producto
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                )

                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("Ingredientes (separados por coma)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de la Imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category.getDisplayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        CategoryType.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.getDisplayName()) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isNotEmpty() && price.isNotEmpty()) {
                            isSaving = true
                            // Recuperamos el objeto original para mantener otros campos si es necesario,
                            // pero aquí reconstruimos
                            // Nota: getItemById es suspend, no podemos llamarlo sync aqui facil.
                            // Asumimos que tenemos los datos en variables.
                            // Pero para update necesitamos el ID original.
                            
                            // Solución: lanzar corrutina para obtener el objeto base y actualizarlo
                            // O simplemente construir uno nuevo con el mismo ID.
                            
                            val updatedItem = com.example.saboresdehogar.model.menu.MenuItem(
                                id = productId,
                                name = name,
                                description = description,
                                price = price.toIntOrNull() ?: 0,
                                category = category,
                                imageUrl = imageUrl.ifBlank { null },
                                isAvailable = true, // Mantener estado
                                ingredients = ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                            
                            menuViewModel.updateProduct(updatedItem) {
                                isSaving = false
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isSaving && name.isNotEmpty() && price.isNotEmpty()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Actualizar Producto")
                    }
                }
            }
        }
    }
}
