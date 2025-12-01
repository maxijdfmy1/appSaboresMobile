package com.example.saboresdehogar.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.saboresdehogar.model.cart.CartItem

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.menuItem.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = item.menuItem.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre y Precio
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.menuItem.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "CLP $${String.format("%,d", item.menuItem.price)} c/u",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Controles de Cantidad
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Botón -
            IconButton(
                onClick = { onDecrement(item.menuItem.id) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Remove, "Quitar uno")
            }

            // Cantidad
            Text(
                text = "${item.quantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Botón +
            IconButton(
                onClick = { onIncrement(item.menuItem.id) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Add, "Añadir uno")
            }

            // Botón Eliminar
            IconButton(
                onClick = { onRemove(item.menuItem.id) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    "Eliminar del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}