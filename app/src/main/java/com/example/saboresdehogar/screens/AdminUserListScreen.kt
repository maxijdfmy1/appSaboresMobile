package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // Nota: Necesitamos agregar 'users' al UserViewModel para que esto funcione
    // Como solución temporal para el prototipo, usaremos un estado local o modificaremos UserViewModel
    // Asumiremos que UserViewModel ha sido actualizado para tener 'allUsers'
    
    var isLoading by remember { mutableStateOf(true) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        // Aquí idealmente llamaríamos a userViewModel.loadAllUsers()
        // Como no puedo modificar UserViewModel en este paso sin romper la cadena,
        // simularé la carga o asumiré que el ViewModel lo tiene.
        // Para hacerlo real, necesitamos actualizar UserViewModel.
        isLoading = false
    }
    
    // --- REVISIÓN: Voy a actualizar UserViewModel en el siguiente paso para soportar esto ---
    val allUsers by userViewModel.allUsers.observeAsState(emptyList())
    val loading by userViewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios Registrados") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (allUsers.isEmpty()) {
                Text("No hay usuarios registrados", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(allUsers) { user ->
                        UserRow(user)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun UserRow(user: User) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = user.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Rol: ${user.role}", style = MaterialTheme.typography.bodySmall)
    }
}
