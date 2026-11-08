package com.example.sistema_inventario.ui.screens // Ajusta según tu paquete real

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class EquipoInfo(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val equipos = listOf(
        EquipoInfo(
            titulo = "Computadoras",
            descripcion = "Equipos de cómputo de escritorio y portátiles asignados al personal para el desarrollo de operaciones y soporte técnico.",
            icono = Icons.Default.Computer
        ),
        EquipoInfo(
            titulo = "Impresoras",
            descripcion = "Dispositivos periféricos de impresión y digitalización para la documentación institucional.",
            icono = Icons.Default.Print
        ),
        EquipoInfo(
            titulo = "UPS (Sistemas de Alimentación Ininterrumpida)",
            descripcion = "Respaldo de energía eléctrica para proteger los equipos críticos ante cortes de energía.",
            icono = Icons.Default.ElectricBolt
        ),
        EquipoInfo(
            titulo = "Otros Activos",
            descripcion = "Dispositivos de red, accesorios y componentes tecnológicos diversos registrados en el inventario.",
            icono = Icons.Default.DevicesOther
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de los Equipos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Catálogo de Activos - SIR Android",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Detalle de las categorías de equipos gestionados en el sistema de inventario institucional.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(equipos) { equipo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = equipo.icono,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = equipo.titulo,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = equipo.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}