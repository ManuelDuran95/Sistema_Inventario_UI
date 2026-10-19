package com.example.sistemainventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAtras(titulo: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(titulo, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
    )
}

// Pantalla 1: Levantar Requerimiento
@Composable
fun LevantarReqScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    var cod by remember { mutableStateOf("") }
    var falla by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }

    Scaffold(topBar = { TopBarAtras("Nuevo Requerimiento", onBack) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Detalle de la Falla", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = cod, onValueChange = { cod = it }, label = { Text("Código Inventario") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = falla, onValueChange = { falla = it }, label = { Text("Falla Detectada") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario Solicitante") }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.levantarRequerimiento(cod, falla, usuario) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) { Text("Guardar Ticket") }

                    if (viewModel.mensajeReq.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(viewModel.mensajeReq, color = if (viewModel.mensajeReq.contains("Error")) Color.Red else Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

// Pantalla 2: Actualizar Requerimiento
@Composable
fun ActualizarReqScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    var idReq by remember { mutableStateOf("") }

    Scaffold(topBar = { TopBarAtras("Actualizar Estado", onBack) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Finalizar Requerimiento", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = idReq, onValueChange = { idReq = it }, label = { Text("ID del Requerimiento") }, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.actualizarRequerimiento(idReq) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) { Text("Marcar como Finalizado") }

                    if (viewModel.mensajeUpdate.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(viewModel.mensajeUpdate, color = if (viewModel.mensajeUpdate.contains("❌")) Color.Red else Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

// Pantalla 3: Ver Reporte
@Composable
fun VerReporteScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { viewModel.cargarReporte() }

    Scaffold(topBar = { TopBarAtras("Reporte Gerencial", onBack) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Resumen del Sistema", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = viewModel.textoReporte,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}

// Pantalla 4: Forzar Error
@Composable
fun ForzarErrorScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    Scaffold(topBar = { TopBarAtras("Prueba de Logs", onBack) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Simulación de Fallo", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Probará la escritura de logs", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.forzarError() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)) // Rojo
                    ) { Text("Ejecutar Excepción") }

                    if (viewModel.mensajeErrorSimulado.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(viewModel.mensajeErrorSimulado, color = Color(0xFFE11D48), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}