package com.example.saboresdehogar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.screens.components.ProductCard
import com.example.saboresdehogar.viewmodel.CartViewModel
import com.example.saboresdehogar.viewmodel.MenuViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel
) {
    val categories by menuViewModel.menuCategories.observeAsState(emptyList())
    val menuItems by menuViewModel.menuItems.observeAsState(emptyList())
    val selectedCategory by menuViewModel.selectedCategory.observeAsState()
    val isLoading by menuViewModel.isLoading.observeAsState(false)
    val error by menuViewModel.error.observeAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        menuViewModel.loadMenu()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ESTADO: CARGANDO
            if (isLoading) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Conectando con API...")
                    }
                }
            } 
            // ESTADO: ERROR
            else if (error != null) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚠️ Error de conexión",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$error", 
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { menuViewModel.loadMenu() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            // ESTADO: LISTA VACÍA (pero sin error)
            else if (categories.isEmpty()) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos.")
                }
            }
            // ESTADO: ÉXITO (Mostrar lista)
            else {
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory?.let { cat -> categories.indexOfFirst { it.type == cat } + 1 } ?: 0,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedCategory == null,
                        onClick = { menuViewModel.filterByCategory(null) },
                        text = { Text("Todos", fontWeight = if (selectedCategory == null) FontWeight.Bold else FontWeight.Normal) }
                    )
                    categories.forEach { category ->
                        Tab(
                            selected = selectedCategory == category.type,
                            onClick = { menuViewModel.filterByCategory(category.type) },
                            text = {
                                Text(
                                    text = category.displayName,
                                    fontWeight = if (selectedCategory == category.type) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(menuItems) { item ->
                        ProductCard(
                            item = item,
                            onCardClick = { productId ->
                                navController.navigate(Screen.ProductDetail.createRoute(productId))
                            },
                            onAddToCartClick = {
                                cartViewModel.addItem(it)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Añadido correctamente",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}