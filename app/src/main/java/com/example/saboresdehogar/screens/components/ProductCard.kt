package com.example.saboresdehogar.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saboresdehogar.R
import com.example.saboresdehogar.model.menu.CategoryType
import com.example.saboresdehogar.model.menu.MenuItem
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    item: MenuItem,
    onCardClick: (String) -> Unit,
    onAddToCartClick: (MenuItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onCardClick(item.id) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Imagen del producto
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .error(R.drawable.ic_launcher_foreground) // Si falla, muestra el logo
                    .placeholder(R.drawable.ic_launcher_foreground) // Mientras carga, muestra el logo
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            // Contenido de texto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CLP $${String.format("%,d", item.price)}", // Formato de moneda
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onAddToCartClick(item) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar al Carrito")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    SaboresDeHogarTheme {
        ProductCard(
            item = MenuItem(
                id = "plato008",
                name = "Paila Marina",
                description = "Exquisita sopa de mariscos frescos del Pacífico con un toque de vino blanco y cilantro.",
                price = 9500,
                category = CategoryType.PLATOS_PRINCIPALES,
                imageUrl = "https://via.placeholder.com/400x300/C1272D/FFFFFF?text=Paila+Marina",
                isVegetarian = false,
                isAvailable = true
            ),
            onCardClick = {},
            onAddToCartClick = {}
        )
    }
}