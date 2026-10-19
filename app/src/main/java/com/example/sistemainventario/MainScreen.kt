package com.example.sistemainventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class OpcionMenu(val titulo: String, val icono: ImageVector, val accion: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onIrAListar: () -> Unit,
    onIrARegistrar: () -> Unit
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    val opciones = listOf(
        OpcionMenu("Listar Activos", Icons.Default.List) {
            onIrAListar()
        },
        OpcionMenu("Registrar Activo", Icons.Default.AddCircle) {
            onIrARegistrar()
        },
        OpcionMenu("Levantar Req.", Icons.Default.Build) {
            // TODO: Navegar a pantalla de requerimiento
        },
        OpcionMenu("Actualizar Req.", Icons.Default.Refresh) {
            // TODO: Navegar a pantalla de actualizar estado
        },
        OpcionMenu("Ver Reporte", Icons.Default.Info) {
            // TODO: Navegar a reporte gerencial
        },
        OpcionMenu("Forzar Error", Icons.Default.Warning) {
            // TODO: Lógica de forzar error
        }
    )

    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas salir del sistema?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoLogout = false
                        authViewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                ) {
                    Text("Sí, salir", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú Principal", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B)),
                actions = {
                    IconButton(onClick = { mostrarDialogoLogout = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(opciones) { opcion ->
                    MenuCard(opcion = opcion)
                }
            }
        }
    }
}

@Composable
fun MenuCard(opcion: OpcionMenu) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { opcion.accion() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = opcion.icono,
                contentDescription = opcion.titulo,
                modifier = Modifier.size(54.dp),
                tint = Color(0xFF3B82F6)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = opcion.titulo,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
