package com.example.saboresdehogar.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.saboresdehogar.components.Screen
import com.example.saboresdehogar.model.user.UserRole
import com.example.saboresdehogar.ui.theme.SaboresDeHogarTheme
import com.example.saboresdehogar.util.ValidationUtils
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.AuthState
import com.example.saboresdehogar.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var name by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados de validación
    var isRutError by remember { mutableStateOf(false) }
    var isPasswordMismatch by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) } // Nuevo
    var isPhoneError by remember { mutableStateOf(false) } // Nuevo

    val authState by authViewModel.authState.observeAsState()
    val authResponse by authViewModel.authResponse.observeAsState()

    // Limpia el error al entrar
    LaunchedEffect(Unit) {
        authViewModel.clearAuthResponse()
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val userRole = authViewModel.currentUser.value?.role
            val destination = if (userRole == UserRole.ADMIN) {
                Screen.AdminProductList.route
            } else {
                Screen.Catalog.route
            }
            navController.navigate(destination) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Completa tus datos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Nombre ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- RUT ---
            OutlinedTextField(
                value = rut,
                onValueChange = {
                    rut = it
                    isRutError = false // Limpia el error al escribir
                },
                label = { Text("RUT (Ej: 12345678-9)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isRutError,
                supportingText = { if (isRutError) Text("El RUT no es válido") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Email ---
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    isEmailError = false
                },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isEmailError,
                supportingText = { if (isEmailError) Text("Formato de correo inválido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Teléfono ---
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    isPhoneError = false
                },
                label = { Text("Teléfono (Ej: 912345678)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPhoneError,
                supportingText = { if (isPhoneError) Text("Número inválido (Use 9 dígitos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Dirección ---
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Contraseña ---
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isPasswordMismatch = false
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPasswordMismatch,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Mostrar/Ocultar")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Confirmar Contraseña ---
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isPasswordMismatch = false
                },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPasswordMismatch,
                supportingText = { if (isPasswordMismatch) Text("Las contraseñas no coinciden") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Error Message (General) ---
            authResponse?.let {
                if (!it.success && it.message != null) {
                    Text(
                        text = it.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // --- Register Button ---
            Button(
                onClick = {
                    // Validaciones
                    isRutError = !ValidationUtils.isRutValid(rut)
                    isEmailError = !ValidationUtils.isValidEmail(email)
                    isPhoneError = !ValidationUtils.isValidPhone(phone)
                    isPasswordMismatch = password != confirmPassword

                    if (!isRutError && !isEmailError && !isPhoneError && !isPasswordMismatch) {
                        authViewModel.register(email, password, name, phone, rut, address)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = authState != AuthState.Loading
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SaboresDeHogarTheme {
        RegisterScreen(
            navController = rememberNavController(),
            authViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
        )
    }
}
