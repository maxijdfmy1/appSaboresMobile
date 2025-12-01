package com.example.saboresdehogar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saboresdehogar.R
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel
) {
    var item by remember { mutableStateOf<MenuItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado local para personalización
    var quantity by remember { mutableStateOf(1) }
    // Mapa de ingredientes a incluir (true = incluir, false = quitar/sin)
    // Inicialmente todos incluidos (true)
    val ingredientsState = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(productId) {
        if (productId != null) {
            val loadedItem = menuViewModel.getItemById(productId)
            item = loadedItem
            // Inicializar ingredientes
            loadedItem?.ingredients?.forEach { ingredient ->
                ingredientsState[ingredient] = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (item != null) {
            val currentItem = item!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp) // Espacio para botón flotante/inferior
            ) {
                 AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentItem.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = currentItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray),
                    onError = { 
                        // No hacemos nada, el fondo rojo lo manejará el placeholder
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = currentItem.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "CLP $${String.format("%,d", currentItem.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentItem.description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                    )
                    
                    // --- SECCIÓN DE INGREDIENTES (PERSONALIZACIÓN) ---
                    if (currentItem.ingredients.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Personaliza tu pedido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Desmarca los ingredientes que quieras quitar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        currentItem.ingredients.forEach { ingredient ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val current = ingredientsState[ingredient] ?: true
                                        ingredientsState[ingredient] = !current
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = ingredientsState[ingredient] ?: true,
                                    onCheckedChange = { checked ->
                                        ingredientsState[ingredient] = checked
                                    }
                                )
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- SELECTOR DE CANTIDAD ---
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Remove, "Menos")
                        }
                        
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, "Más")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Construir notas basadas en ingredientes excluidos
                            val excludedIngredients = currentItem.ingredients.filter { ingredient ->
                                ingredientsState[ingredient] == false
                            }
                            
                            val notes = if (excludedIngredients.isNotEmpty()) {
                                "Sin: " + excludedIngredients.joinToString(", ")
                            } else {
                                null
                            }

                            cartViewModel.addItem(currentItem, quantity, notes)
                            
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Añadido correctamente ($quantity)",
                                    duration = SnackbarDuration.Short
                                )
                                // Opcional: Volver atrás o ir al carrito
                                // navController.popBackStack() 
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Agregar al Carrito", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (productId == null) {
                    Text("Error: ID de producto no válido.")
                } else {
                    CircularProgressIndicator()
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailScreenPreview() {
    SaboresDeHogarTheme {
        // Preview placeholder
        Box(Modifier.fillMaxSize()) { Text("Preview") }
    }
}
