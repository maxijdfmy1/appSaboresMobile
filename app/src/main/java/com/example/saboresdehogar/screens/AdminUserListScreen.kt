package com.example.saboresdehogar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saboresdehogar.model.user.ActualizarUsuarioDto
import com.example.saboresdehogar.model.user.User
import com.example.saboresdehogar.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserListScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val allUsers by userViewModel.allUsers.observeAsState(emptyList())
    val loading by userViewModel.isLoading.observeAsState(false)

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

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
                        UserRow(
                            user = user,
                            onEditClick = {
                                selectedUser = user
                                showEditDialog = true
                            },
                            onDeleteClick = {
                                selectedUser = user
                                showDeleteDialog = true
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { userDto ->
                userViewModel.updateUser(selectedUser!!.id, userDto)
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog && selectedUser != null) {
        DeleteConfirmationDialog(
            userName = selectedUser!!.name,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                userViewModel.deleteUser(selectedUser!!.id)
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun UserRow(
    user: User,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Rol: ${user.role}", style = MaterialTheme.typography.bodySmall)
        }
        Row {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Usuario")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar Usuario")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (ActualizarUsuarioDto) -> Unit
) {
    var nombre by remember { mutableStateOf(user.name) }
    var rut by remember { mutableStateOf(user.rut ?: "") }
    var email by remember { mutableStateOf(user.email) }
    // SOLUCIÓN: Usar `user.phone` que es el campo correcto en el modelo.
    var telefono by remember { mutableStateOf(user.phone) }
    // SOLUCIÓN: Usar getFullAddress() para obtener un String y manejar el caso nulo.
    var direccion by remember { mutableStateOf(user.getDefaultAddress()?.getFullAddress() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") })
                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
            }
        },
        confirmButton = {
            Button(onClick = {
                // SOLUCIÓN: El `as String` ya no es necesario porque `direccion` es un String.
                val dto = ActualizarUsuarioDto(nombre, rut, email, telefono, direccion)
                onConfirm(dto)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar al usuario '$userName'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
