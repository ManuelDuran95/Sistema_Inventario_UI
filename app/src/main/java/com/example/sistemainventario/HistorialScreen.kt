package com.example.sistemainventario

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class FiltroHistorial(val etiqueta: String) {
    TODOS("Todos"),
    MIOS("Mis tickets"),
    PENDIENTES("Pendientes"),
    FINALIZADOS("Finalizados")
}

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es")).format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(viewModel: InventarioViewModel, onBack: () -> Unit) {
    var filtro by remember { mutableStateOf(FiltroHistorial.TODOS) }
    val uid = viewModel.uidUsuario
    val todos = viewModel.listaRequerimientos

    val visibles = todos
        .filter {
            when (filtro) {
                FiltroHistorial.TODOS -> true
                FiltroHistorial.MIOS -> it.creadoPorUid == uid
                FiltroHistorial.PENDIENTES -> it.estado == "PENDIENTE"
                FiltroHistorial.FINALIZADOS -> it.estado == "FINALIZADO"
            }
        }
        .sortedByDescending { it.id }

    Scaffold(topBar = { TopBarAtras("Historial de Tickets", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenConteo("Total", todos.size, Color(0xFF1E293B), Modifier.weight(1f))
                ResumenConteo("Pendientes", todos.count { it.estado == "PENDIENTE" }, Color(0xFFB45309), Modifier.weight(1f))
                ResumenConteo("Finalizados", todos.count { it.estado == "FINALIZADO" }, Color(0xFF047857), Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroHistorial.entries.forEach { opcion ->
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { filtro = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            if (visibles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay tickets para mostrar", color = Color(0xFF64748B))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visibles, key = { it.docId }) { req ->
                        TicketHistorialCard(req, viewModel.nombreActivo(req.inventario))
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenConteo(titulo: String, valor: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(titulo, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun TicketHistorialCard(req: RequerimientoUiModel, nombreActivo: String?) {
    val finalizado = req.estado == "FINALIZADO"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Ticket #${req.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    req.estado,
                    color = if (finalizado) Color(0xFF047857) else Color(0xFFB45309),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(
                            if (finalizado) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (nombreActivo != null) "${req.inventario} · $nombreActivo" else req.inventario,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(req.falla, fontSize = 14.sp, color = Color(0xFF475569))
            Spacer(Modifier.height(8.dp))
            LineaTraza("Solicitante", req.usuario)
            LineaTraza(
                "Creado",
                listOfNotNull(req.creadoPor.ifBlank { null }, req.creadoEn?.let(::formatearFecha))
                    .joinToString(" · ")
            )
            if (finalizado) {
                LineaTraza(
                    "Finalizado",
                    listOfNotNull(req.finalizadoPor.ifBlank { null }, req.finalizadoEn?.let(::formatearFecha))
                        .joinToString(" · ")
                )
            }
        }
    }
}

@Composable
private fun LineaTraza(etiqueta: String, valor: String) {
    if (valor.isBlank()) return
    Text(
        "$etiqueta: $valor",
        fontSize = 12.sp,
        color = Color(0xFF64748B)
    )
}
