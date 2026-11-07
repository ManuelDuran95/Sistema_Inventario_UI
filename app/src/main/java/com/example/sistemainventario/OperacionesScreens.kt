package com.example.sistemainventario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAtras(titulo: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(titulo, color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
    )
}

// Pantalla 1: Levantar Requerimiento (Mejorada con selector interactivo de activos)
@Composable
fun LevantarReqScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    var cod by remember { mutableStateOf("") }
    var falla by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf(viewModel.correoUsuario) }
    val guardando = viewModel.guardandoReq
    val activosDisponibles = viewModel.listaActivos

    val fallasComunes = listOf(
        "No enciende",
        "Falla de red / conectividad",
        "Pantalla sin imagen",
        "Mantenimiento preventivo"
    )

    LaunchedEffect(Unit) {
        viewModel.mensajeReq = ""
        if (usuario.isBlank()) usuario = viewModel.correoUsuario
    }

    Scaffold(topBar = { TopBarAtras("Nuevo Requerimiento", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Selecciona un equipo del inventario o ingresa su código para reportar una incidencia en la nube.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "1. Equipo Afectado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )

                    if (activosDisponibles.isNotEmpty()) {
                        Text(
                            text = "Selección rápida de activos registrados:",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activosDisponibles.forEach { activo ->
                                val seleccionado = cod.equals(activo.inventario, ignoreCase = true)
                                Surface(
                                    modifier = Modifier.clickable(enabled = !guardando) {
                                        cod = activo.inventario
                                        viewModel.mensajeReq = ""
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (seleccionado) Color(0xFF3B82F6) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        1.dp,
                                        if (seleccionado) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = activo.inventario,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (seleccionado) Color.White else Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = activo.nombre,
                                            fontSize = 11.sp,
                                            color = if (seleccionado) Color(0xFFDBEAFE) else Color(0xFF64748B),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cod,
                        onValueChange = { cod = it; viewModel.mensajeReq = "" },
                        label = { Text("Código de Inventario") },
                        placeholder = { Text("Ej. PC-001") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        singleLine = true,
                        enabled = !guardando,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    Text(
                        text = "2. Detalle del Problema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fallasComunes.forEach { sugerencia ->
                            Surface(
                                modifier = Modifier.clickable(enabled = !guardando) {
                                    falla = sugerencia
                                    viewModel.mensajeReq = ""
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "+ $sugerencia",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = falla,
                        onValueChange = { falla = it; viewModel.mensajeReq = "" },
                        label = { Text("Falla Detectada") },
                        placeholder = { Text("Describe el problema que presenta el equipo...") },
                        leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        enabled = !guardando,
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = usuario,
                        onValueChange = { usuario = it; viewModel.mensajeReq = "" },
                        label = { Text("Usuario Solicitante") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        singleLine = true,
                        enabled = !guardando,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (viewModel.mensajeReq.isNotEmpty()) {
                        val esError = viewModel.mensajeReq.contains("Error", ignoreCase = true) ||
                                viewModel.mensajeReq.contains("❌") ||
                                viewModel.mensajeReq.contains("No existe", ignoreCase = true) ||
                                viewModel.mensajeReq.contains("obligatorio", ignoreCase = true)

                        Surface(
                            color = if (esError) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (esError) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (esError) Color(0xFFE11D48) else Color(0xFF059669)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = viewModel.mensajeReq,
                                    color = if (esError) Color(0xFFE11D48) else Color(0xFF059669),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.levantarRequerimiento(cod, falla, usuario) {
                                cod = ""
                                falla = ""
                                usuario = viewModel.correoUsuario
                            }
                        },
                        enabled = !guardando,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
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
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F5F9)).padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Finalizar Requerimiento", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E293B))
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
                        shape = RoundedCornerShape(12.dp),
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
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    if (finalizando) {
                                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Marcar como Finalizado", fontWeight = FontWeight.Bold)
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
    Text("Tickets pendientes (toca uno para seleccionar)", fontWeight = FontWeight.SemiBold, color = Color(0xFF475569), fontSize = 14.sp)
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

// Pantalla 3: Ver Reporte Gerencial (Dashboard Ejecutivo interactivo)
@Composable
fun VerReporteScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { viewModel.cargarReporte() }

    val totalActivos = viewModel.listaActivos.size
    val activosCC01 = viewModel.listaActivos.count { it.cc.equals("CC01", ignoreCase = true) }
    val activosCC02 = viewModel.listaActivos.count { it.cc.equals("CC02", ignoreCase = true) }

    val totalTickets = viewModel.listaRequerimientos.size
    val ticketsPendientes = viewModel.listaRequerimientos.count { it.estado == "PENDIENTE" }
    val ticketsFinalizados = viewModel.listaRequerimientos.count { it.estado == "FINALIZADO" }
    val tasaResolucion = if (totalTickets > 0) (ticketsFinalizados * 100) / totalTickets else 0

    Scaffold(topBar = { TopBarAtras("Reporte Gerencial", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjetas KPI superiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    titulo = "Activos",
                    valor = totalActivos.toString(),
                    subtitulo = "En inventario",
                    colorValor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    titulo = "Pendientes",
                    valor = ticketsPendientes.toString(),
                    subtitulo = "Por atender",
                    colorValor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    titulo = "Resueltos",
                    valor = "$tasaResolucion%",
                    subtitulo = "$ticketsFinalizados de $totalTickets",
                    colorValor = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }

            // Distribución por Centro de Costo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Distribución por Centro de Costo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CC01 • Informática ISSS", fontSize = 14.sp, color = Color(0xFF475569))
                        Text("$activosCC01 equipos", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }
                    LinearProgressIndicator(
                        progress = if (totalActivos > 0) activosCC01.toFloat() / totalActivos else 0f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFFE2E8F0)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CC02 • Mantenimiento General", fontSize = 14.sp, color = Color(0xFF475569))
                        Text("$activosCC02 equipos", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    }
                    LinearProgressIndicator(
                        progress = if (totalActivos > 0) activosCC02.toFloat() / totalActivos else 0f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            // Detalle consolidado del sistema (generado por el ViewModel)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Detalle Consolidado", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                        IconButton(onClick = { viewModel.cargarReporte() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color(0xFF3B82F6))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = viewModel.textoReporte.ifBlank { "Cargando datos del reporte..." },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    colorValor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Text(valor, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = colorValor)
            Text(subtitulo, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}

// Pantalla 4: Forzar Error (Módulo de Auditoría y Bitácora de Excepciones)
@Composable
fun ForzarErrorScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    var historialLogs by remember { mutableStateOf(listOf<String>()) }

    Scaffold(topBar = { TopBarAtras("Prueba de Logs", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Simulación de Fallo Controlado", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Genera una excepción de prueba para verificar la captura de errores y escritura en bitácora del sistema.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.forzarError()
                            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            historialLogs = listOf("[$hora] ERROR: Excepción controlada capturada -> ${viewModel.mensajeErrorSimulado}") + historialLogs
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                    ) {
                        Text("Ejecutar Excepción", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                    }

                    if (viewModel.mensajeErrorSimulado.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = viewModel.mensajeErrorSimulado,
                                color = Color(0xFFE11D48),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            if (historialLogs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CONSOLA DE REGISTRO (LOGS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(Modifier.height(8.dp))
                        historialLogs.forEach { log ->
                            Text(
                                text = log,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = Color(0xFFF87171),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}