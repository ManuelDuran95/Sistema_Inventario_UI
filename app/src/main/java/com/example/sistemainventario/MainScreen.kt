package com.example.sistemainventario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

data class OpcionMenu(
    val titulo: String,
    val subtitulo: String,
    val icono: ImageVector,
    val colorIcono: Color,
    val colorFondoIcono: Color,
    val accion: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onLogout: () -> Unit,
    onIrAListar: () -> Unit,
    onIrARegistrar: () -> Unit
) {
    var mostrarDialogoLogout by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val correoActual = FirebaseAuth.getInstance().currentUser?.email ?: "Sesión activa"

    val opcionesInventario = listOf(
        OpcionMenu(
            titulo = "Listar Activos",
            subtitulo = "Consultar equipos",
            icono = Icons.Default.List,
            colorIcono = Color(0xFF2563EB),
            colorFondoIcono = Color(0xFFDBEAFE),
            accion = { onIrAListar() }
        ),
        OpcionMenu(
            titulo = "Registrar Activo",
            subtitulo = "Nuevo ingreso",
            icono = Icons.Default.AddCircle,
            colorIcono = Color(0xFF4F46E5),
            colorFondoIcono = Color(0xFFE0E7FF),
            accion = { onIrARegistrar() }
        )
    )

    val opcionesSoporte = listOf(
        OpcionMenu(
            titulo = "Levantar Req.",
            subtitulo = "Reportar falla",
            icono = Icons.Default.Build,
            colorIcono = Color(0xFFD97706),
            colorFondoIcono = Color(0xFFFEF3C7),
            accion = { navController.navigate("levantar_req") }
        ),
        OpcionMenu(
            titulo = "Actualizar Req.",
            subtitulo = "Finalizar ticket",
            icono = Icons.Default.Refresh,
            colorIcono = Color(0xFF059669),
            colorFondoIcono = Color(0xFFD1FAE5),
            accion = { navController.navigate("actualizar_req") }
        ),
        OpcionMenu(
            titulo = "Historial",
            subtitulo = "Bitácora de tickets",
            icono = Icons.Default.DateRange,
            colorIcono = Color(0xFF7C3AED),
            colorFondoIcono = Color(0xFFEDE9FE),
            accion = { navController.navigate("historial") }
        ),
        OpcionMenu(
            titulo = "Ver Reporte",
            subtitulo = "Métricas y KPIs",
            icono = Icons.Default.Info,
            colorIcono = Color(0xFF0891B2),
            colorFondoIcono = Color(0xFFCFFAFE),
            accion = { navController.navigate("reporte") }
        )
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
                        authViewModel.logout(context)
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
                title = {
                    Column {
                        Text(
                            text = "Sistema de Inventario",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = correoActual,
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1: Inventario
            Text(
                text = "GESTIÓN DE INVENTARIO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                opcionesInventario.forEach { opcion ->
                    MenuCard(opcion = opcion, modifier = Modifier.weight(1f))
                }
            }

            // Sección 2: Operaciones y Soporte
            Text(
                text = "MESA DE AYUDA Y REPORTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                opcionesSoporte.take(2).forEach { opcion ->
                    MenuCard(opcion = opcion, modifier = Modifier.weight(1f))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                opcionesSoporte.drop(2).forEach { opcion ->
                    MenuCard(opcion = opcion, modifier = Modifier.weight(1f))
                }
            }

            // Sección 3: Diagnóstico
            Text(
                text = "DIAGNÓSTICO DEL SISTEMA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("forzar_error") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Forzar Error",
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Forzar Error y Bitácora",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Simular excepción controlada para prueba de logs",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MenuCard(opcion: OpcionMenu, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { opcion.accion() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(opcion.colorFondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = opcion.icono,
                    contentDescription = opcion.titulo,
                    modifier = Modifier.size(26.dp),
                    tint = opcion.colorIcono
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = opcion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = opcion.subtitulo,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}