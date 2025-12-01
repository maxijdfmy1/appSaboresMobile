package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme

@Composable
fun AboutUsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "¿Quiénes Somos?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Somos una familia chilena dedicada a preservar y compartir los sabores tradicionales de nuestro país. Desde hace más de 3 años, hemos trabajado con pasión para ofrecer platos auténticos que representen la rica gastronomía chilena.\n\nNuestro restaurante nació del sueño de Maximiliano, quien llegó desde el sur de Chile con sus recetas familiares y la determinación de mostrar al mundo los verdaderos sabores de nuestra tierra.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nuestra Misión",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Preservar y difundir la tradición culinaria chilena, ofreciendo platos preparados con ingredientes frescos y técnicas tradicionales, manteniendo viva la herencia gastronómica de nuestros ancestros.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Nuestros Valores",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        ValueItem(title = "Tradición:", description = "Respetamos las recetas originales transmitidas de generación en generación")
        ValueItem(title = "Calidad:", description = "Seleccionamos cuidadosamente cada ingrediente")
        ValueItem(title = "Autenticidad:", description = "Cada plato refleja el verdadero sabor chileno")
        ValueItem(title = "Familia:", description = "Tratamos a cada cliente como parte de nuestra familia")
    }
}

@Composable
fun ValueItem(title: String, description: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutUsScreenPreview() {
    SaboresDeHogarTheme {
        AboutUsScreen()
    }
}
