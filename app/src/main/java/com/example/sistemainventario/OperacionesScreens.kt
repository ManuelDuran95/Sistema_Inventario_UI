package com.example.sistemainventario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
    var usuario by remember { mutableStateOf(viewModel.correoUsuario) }
    val guardando = viewModel.guardandoReq

    LaunchedEffect(Unit) { viewModel.mensajeReq = "" }

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
                    OutlinedTextField(value = cod, onValueChange = { cod = it; viewModel.mensajeReq = "" }, label = { Text("Código Inventario") }, singleLine = true, enabled = !guardando, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = falla, onValueChange = { falla = it; viewModel.mensajeReq = "" }, label = { Text("Falla Detectada") }, enabled = !guardando, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = usuario, onValueChange = { usuario = it; viewModel.mensajeReq = "" }, label = { Text("Usuario Solicitante") }, singleLine = true, enabled = !guardando, modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.levantarRequerimiento(cod, falla, usuario) {
                                cod = ""
                                falla = ""
                                usuario = viewModel.correoUsuario
                            }
                        },
                        enabled = !guardando,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Guardar Ticket")
                        }
                    }

                    if (viewModel.mensajeReq.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(viewModel.mensajeReq, color = if (viewModel.mensajeReq.contains("Error")) Color(0xFFE11D48) else Color(0xFF059669))
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
    val finalizando = viewModel.finalizandoReq
    val ticket = viewModel.buscarRequerimiento(idReq)
    val pendientes = viewModel.listaRequerimientos
        .filter { it.estado == "PENDIENTE" }
        .sortedBy { it.id }

    LaunchedEffect(Unit) { viewModel.mensajeUpdate = "" }

    Scaffold(topBar = { TopBarAtras("Actualizar Estado", onBack) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Finalizar Requerimiento", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = idReq,
                        onValueChange = { nuevo ->
                            idReq = nuevo.filter { it.isDigit() }
                            viewModel.mensajeUpdate = ""
                        },
                        label = { Text("ID del Requerimiento") },
                        singleLine = true,
                        enabled = !finalizando,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    when {
                        idReq.isBlank() -> ListaPendientes(pendientes) { idReq = it.id.toString() }
                        ticket == null -> Text(
                            "No existe un requerimiento con el ID #$idReq.",
                            color = Color(0xFFE11D48)
                        )
                        else -> {
                            ResumenTicket(ticket, viewModel.nombreActivo(ticket.inventario))
                            Spacer(Modifier.height(20.dp))
                            if (ticket.estado == "FINALIZADO") {
                                Text(
                                    "Este ticket ya está finalizado.",
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Button(
                                    onClick = { viewModel.actualizarRequerimiento(idReq) },
                                    enabled = !finalizando,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    if (finalizando) {
                                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Marcar como Finalizado")
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { idReq = ""; viewModel.mensajeUpdate = "" }, enabled = !finalizando) {
                                Text("Buscar otro ticket")
                            }
                        }
                    }

                    if (viewModel.mensajeUpdate.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(viewModel.mensajeUpdate, color = if (viewModel.mensajeUpdate.contains("❌")) Color(0xFFE11D48) else Color(0xFF059669))
                    }
                }
            }
        }
    }
}

@Composable
private fun ListaPendientes(pendientes: List<RequerimientoUiModel>, onSeleccionar: (RequerimientoUiModel) -> Unit) {
    if (pendientes.isEmpty()) {
        Text("No hay tickets pendientes.", color = Color(0xFF475569))
        return
    }
    Text("Tickets pendientes", fontWeight = FontWeight.SemiBold, color = Color(0xFF475569), fontSize = 14.sp)
    Spacer(Modifier.height(8.dp))
    pendientes.forEach { req ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                .clickable { onSeleccionar(req) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("#${req.id}", fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(req.inventario, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(req.falla, color = Color(0xFF475569), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ResumenTicket(ticket: RequerimientoUiModel, nombreActivo: String?) {
    val finalizado = ticket.estado == "FINALIZADO"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ticket #${ticket.id}", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text(
                ticket.estado,
                color = if (finalizado) Color(0xFF047857) else Color(0xFFB45309),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .background(
                        if (finalizado) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        DatoTicket("Activo", if (nombreActivo != null) "${ticket.inventario} · $nombreActivo" else ticket.inventario)
        DatoTicket("Falla", ticket.falla)
        DatoTicket("Solicitante", ticket.usuario)
    }
}

@Composable
private fun DatoTicket(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(etiqueta, color = Color(0xFF64748B), fontSize = 12.sp)
        Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
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