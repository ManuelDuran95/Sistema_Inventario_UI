package com.example.sistemainventario

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroActivoScreen(
    viewModel: InventarioViewModel,
    onVolver: () -> Unit
) {
    var inventario by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var ccSeleccionado by remember { mutableStateOf("CC01") }

    val tiposPredefinidos = listOf("PC", "Monitor", "IMPRESORA", "UPS", "Otro")
    var tipoSeleccionado by remember { mutableStateOf("Monitor") }
    var otroTipoTexto by remember { mutableStateOf("") }

    var mensajeExito by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val centrosCosto = listOf(
        "CC01" to "Informática ISSS",
        "CC02" to "Mantenimiento General"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Activo", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B)),
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado limpio
            Text(
                text = "Completa la información para registrar el equipo en el inventario.",
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Selector intuitivo de Tipo de Equipo
                    Text(
                        text = "1. Categoría del Equipo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tiposPredefinidos.take(3).forEach { tipo ->
                                ChipSeleccionable(
                                    texto = tipo,
                                    seleccionado = tipoSeleccionado == tipo,
                                    onClick = {
                                        tipoSeleccionado = tipo
                                        mensajeExito = false
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tiposPredefinidos.drop(3).forEach { tipo ->
                                ChipSeleccionable(
                                    texto = tipo,
                                    seleccionado = tipoSeleccionado == tipo,
                                    onClick = {
                                        tipoSeleccionado = tipo
                                        mensajeExito = false
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    if (tipoSeleccionado == "Otro") {
                        OutlinedTextField(
                            value = otroTipoTexto,
                            onValueChange = { otroTipoTexto = it },
                            label = { Text("Especificar tipo") },
                            placeholder = { Text("Ej. Proyector, Router, Scanner") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF3B82F6)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    // 2. Datos de Identificación (Cajas uniformes de 1 línea)
                    Text(
                        text = "2. Identificación",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    OutlinedTextField(
                        value = inventario,
                        onValueChange = {
                            inventario = it
                            mensajeError = ""
                            mensajeExito = false
                        },
                        label = { Text("Código de Inventario") },
                        placeholder = { Text("Ej. MON-001") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            mensajeError = ""
                            mensajeExito = false
                        },
                        label = { Text("Descripción o Modelo") },
                        placeholder = { Text("Ej. Monitor AOC 24 pulgadas") },
                        leadingIcon = { Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Divider(color = Color(0xFFF1F5F9))

                    // 3. Selector de Centro de Costo (Sin tener que teclear CC01/CC02)
                    Text(
                        text = "3. Centro de Costo Asignado",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        centrosCosto.forEach { (codigo, depto) ->
                            val activo = ccSeleccionado == codigo
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { ccSeleccionado = codigo },
                                shape = RoundedCornerShape(12.dp),
                                color = if (activo) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                                border = BorderStroke(
                                    width = if (activo) 2.dp else 1.dp,
                                    color = if (activo) Color(0xFF3B82F6) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = codigo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (activo) Color(0xFF1D4ED8) else Color(0xFF475569)
                                    )
                                    Text(
                                        text = depto,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Mensajes de alerta con fondo suave
                    if (mensajeError.isNotEmpty()) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = mensajeError, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    if (mensajeExito) {
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Activo guardado correctamente en el inventario.", color = Color(0xFF15803D), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Botón principal
                    Button(
                        onClick = {
                            val codLimpio = inventario.trim().uppercase()
                            val nomLimpio = nombre.trim()
                            val tipoFinal = if (tipoSeleccionado == "Otro") otroTipoTexto.trim() else tipoSeleccionado

                            if (codLimpio.isEmpty() || nomLimpio.isEmpty() || tipoFinal.isEmpty()) {
                                mensajeError = "Por favor completa el código y la descripción."
                                mensajeExito = false
                            } else if (viewModel.listaActivos.any { it.inventario.equals(codLimpio, ignoreCase = true) }) {
                                mensajeError = "El código $codLimpio ya está registrado."
                                mensajeExito = false
                            } else {
                                viewModel.registrarActivo(codLimpio, nomLimpio, ccSeleccionado, tipoFinal)
                                mensajeExito = true
                                mensajeError = ""
                                inventario = ""
                                nombre = ""
                                otroTipoTexto = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Activo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChipSeleccionable(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) Color(0xFF3B82F6) else Color(0xFFF1F5F9),
        border = if (seleccionado) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (seleccionado) Color.White else Color(0xFF475569)
            )
        }
    }
}