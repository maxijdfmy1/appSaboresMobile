
package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme
import java.util.Calendar

@Composable
fun CheckoutScreen(navController: NavController, total: Int) {
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var isCardNumberError by remember { mutableStateOf(false) }
    var isExpiryDateError by remember { mutableStateOf(false) }
    var isCvvError by remember { mutableStateOf(false) }

    fun validateFields() {
        isCardNumberError = cardNumber.length != 16
        isCvvError = cvv.length !in 3..4
        isExpiryDateError = !isValidExpiryDate(expiryDate)
    }

    val isFormValid by derivedStateOf {
        cardNumber.length == 16 &&
        cvv.length in 3..4 &&
        isValidExpiryDate(expiryDate) &&
        cardHolderName.isNotBlank()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Total a Pagar: CLP $${String.format("%,d", total)}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cardNumber,
            onValueChange = {
                if (it.length <= 16) {
                    cardNumber = it.filter { char -> char.isDigit() }
                    isCardNumberError = cardNumber.length != 16
                }
            },
            label = { Text("Número de Tarjeta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = isCardNumberError,
            supportingText = { if (isCardNumberError) Text("Debe tener 16 dígitos") },
            visualTransformation = CreditCardVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cardHolderName,
            onValueChange = { cardHolderName = it },
            label = { Text("Nombre del Titular") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { text ->
                     val newText = text.filter { it.isDigit() }
                    if (newText.length <= 4) {
                        expiryDate = when {
                            newText.length >= 3 -> "${newText.substring(0, 2)}/${newText.substring(2)}"
                            else -> newText
                        }
                        isExpiryDateError = !isValidExpiryDate(expiryDate)
                    }
                },
                label = { Text("MM/AA") },
                modifier = Modifier.weight(1f),
                isError = isExpiryDateError,
                supportingText = { if (isExpiryDateError) Text("Fecha inválida") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = cvv,
                onValueChange = {
                    if (it.length <= 4) {
                        cvv = it.filter { char -> char.isDigit() }
                        isCvvError = cvv.length !in 3..4
                    }
                },
                label = { Text("CVV") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                isError = isCvvError,
                supportingText = { if (isCvvError) Text("Debe tener 3-4 dígitos") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                validateFields()
                if (isFormValid) {
                     if (cvv == "777") {
                        navController.navigate(Screen.CheckoutFail.route)
                    } else {
                        val dummyOrderId = "SABORES-${System.currentTimeMillis().toString().takeLast(6)}"
                        navController.navigate(Screen.CheckoutSuccess.createRoute(dummyOrderId))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Pagar")
        }
    }
}

fun isValidExpiryDate(date: String): Boolean {
    if (date.length != 5 || date[2] != '/') return false
    val parts = date.split("/")
    if (parts.size != 2) return false
    val month = parts[0].toIntOrNull() ?: return false
    val year = parts[1].toIntOrNull() ?: return false

    if (month !in 1..12) return false

    val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    return when {
        year > currentYear -> true
        year == currentYear && month >= currentMonth -> true
        else -> false
    }
}

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i < 15) out += " "
        }
        val creditCardOffsetTranslator = object : androidx.compose.ui.text.input.OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset + 1
                if (offset <= 11) return offset + 2
                if (offset <= 16) return offset + 3
                return 19
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return offset
                if (offset <= 9) return offset - 1
                if (offset <= 14) return offset - 2
                if (offset <= 19) return offset - 3
                return 16
            }
        }
        return androidx.compose.ui.text.input.TransformedText(androidx.compose.ui.text.AnnotatedString(out), creditCardOffsetTranslator)
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    SaboresDeHogarTheme {
        CheckoutScreen(navController = rememberNavController(), total = 5000)
    }
}
