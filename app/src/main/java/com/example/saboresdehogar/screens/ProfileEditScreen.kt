package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saboresdehogar.model.user.Address
import com.example.saboresdehogar.util.ValidationUtils
import com.example.saboresdehogar.viewmodel.AuthViewModel
import com.example.saboresdehogar.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navController: NavController
) {
    // Obtenemos el ViewModel
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(context))
    
    val currentUser by authViewModel.currentUser.observeAsState()
    
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    // Inicializar campos
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            phone = user.phone
            address = user.addresses.firstOrNull { it.isDefault }?.street ?: ""
        }
    }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !ValidationUtils.isValidPhone(phone) && phone.isNotEmpty(),
                supportingText = { if (!ValidationUtils.isValidPhone(phone) && phone.isNotEmpty()) Text("Formato inválido") }
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección Principal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Campos de solo lectura
            OutlinedTextField(
                value = currentUser?.email ?: "",
                onValueChange = {},
                label = { Text("Email (No editable)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            
            OutlinedTextField(
                value = currentUser?.rut ?: "",
                onValueChange = {},
                label = { Text("RUT (No editable)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (name.isNotEmpty() && ValidationUtils.isValidPhone(phone)) {
                        isSaving = true
                        errorMessage = null
                        
                        currentUser?.let { user ->
                            // Actualizamos el objeto User
                            val updatedAddress = if (user.addresses.isNotEmpty()) {
                                user.addresses[0].copy(street = address)
                            } else {
                                Address(id = "new", street = address, number = "", comuna = "", isDefault = true)
                            }
                            
                            val updatedUser = user.copy(
                                name = name,
                                phone = phone,
                                addresses = listOf(updatedAddress)
                            )
                            
                            scope.launch {
                                try {
                                    authViewModel.updateProfile(updatedUser)
                                    isSaving = false
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    isSaving = false
                                    errorMessage = "Error al actualizar: ${e.message}"
                                }
                            }
                        }
                    } else {
                        errorMessage = "Por favor revisa los datos ingresados."
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}
