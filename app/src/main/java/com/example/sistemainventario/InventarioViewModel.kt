package com.example.sistemainventario

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Modelos de datos para la UI (Simples y directos)
data class ActivoUiModel(
    val inventario: String,
    val nombre: String,
    val cc: String,
    val tipo: String
)

data class RequerimientoUiModel(
    val id: Int,
    val inventario: String,
    val falla: String,
    val usuario: String,
    var estado: String = "PENDIENTE"
)

class InventarioViewModel : ViewModel() {

    // ==========================================
    // ESTADO DE DATOS (Simulando la base de datos en RAM)
    // ==========================================
    private val _listaActivos = mutableStateListOf<ActivoUiModel>()
    val listaActivos: List<ActivoUiModel> = _listaActivos

    private val _listaRequerimientos = mutableStateListOf<RequerimientoUiModel>()

    // ==========================================
    // ESTADO DE LA INTERFAZ (Mensajes para la UI)
    // ==========================================
    var mensajeReq by mutableStateOf("")
    var mensajeUpdate by mutableStateOf("")
    var textoReporte by mutableStateOf("")
    var mensajeErrorSimulado by mutableStateOf("")

    init {
        // Datos predeterminados para que la lista no empiece vacía
        _listaActivos.add(ActivoUiModel("PC-001", "Laptop Core i7", "CC01", "PC"))
        _listaActivos.add(ActivoUiModel("IMP-001", "Impresora Láser", "CC01", "IMPRESORA"))

        // Datos predeterminados de Requerimientos (Para probar la actualización)
        _listaRequerimientos.add(RequerimientoUiModel(id = 1, inventario = "PC-001", falla = "No enciende la pantalla", usuario = "admin"))
        _listaRequerimientos.add(RequerimientoUiModel(id = 2, inventario = "IMP-001", falla = "Atasco de papel", usuario = "admin"))
    }

    // ==========================================
    // MÉTODOS LÓGICOS DE LA APLICACIÓN
    // ==========================================

    fun registrarActivo(inventario: String, nombre: String, cc: String, tipo: String): Boolean {
        if (inventario.isBlank() || nombre.isBlank()) return false

        // Evitar duplicados
        if (_listaActivos.any { it.inventario.equals(inventario, ignoreCase = true) }) {
            return false
        }

        val nuevo = ActivoUiModel(inventario.trim().uppercase(), nombre.trim(), cc, tipo)
        _listaActivos.add(nuevo)
        return true
    }

    // 1. Levantar Requerimiento
    fun levantarRequerimiento(cod: String, falla: String, usuario: String) {
        val codTrim = cod.trim().uppercase()

        if (codTrim.isEmpty() || falla.isBlank() || usuario.isBlank()) {
            mensajeReq = "Error: Todos los campos son obligatorios."
            return
        }

        // Validar que el activo exista
        val existeActivo = _listaActivos.any { it.inventario.equals(codTrim, ignoreCase = true) }
        if (!existeActivo) {
            mensajeReq = "Error: El activo $codTrim no existe en el sistema."
            return
        }

        // Generar ID autoincrementable y guardar
        val nuevoId = (_listaRequerimientos.maxOfOrNull { it.id } ?: 0) + 1
        _listaRequerimientos.add(RequerimientoUiModel(nuevoId, codTrim, falla, usuario))

        mensajeReq = "✅ Requerimiento #$nuevoId creado (PENDIENTE)."
    }

    // 2. Actualizar Estado
    fun actualizarRequerimiento(idStr: String) {
        val id = idStr.toIntOrNull()
        if (id == null) {
            mensajeUpdate = "⚠️ Ingrese un ID numérico válido."
            return
        }

        // Buscar requerimiento por ID
        val req = _listaRequerimientos.find { it.id == id }
        if (req != null) {
            req.estado = "FINALIZADO"
            mensajeUpdate = "✅ Requerimiento #$id cambiado a [FINALIZADO]."
        } else {
            mensajeUpdate = "❌ Requerimiento no encontrado."
        }
    }

    // 3. Ver Reporte
    fun cargarReporte() {
        // Calcular estadísticas
        val totalPcs = _listaActivos.count { it.tipo.equals("PC", ignoreCase = true) }
        val totalImpresoras = _listaActivos.count { it.tipo.equals("IMPRESORA", ignoreCase = true) }
        val reqPendientes = _listaRequerimientos.count { it.estado == "PENDIENTE" }
        val reqFinalizados = _listaRequerimientos.count { it.estado == "FINALIZADO" }

        textoReporte = """
        +-------------------------------------------------------+
        |             REPORTE CONSOLIDADO DEL SISTEMA           |
        +-------------------------------------------------------+
          Total Computadoras Registradas: $totalPcs
          Total Impresoras Registradas:   $totalImpresoras
          
          Requerimientos en Estado PENDIENTE: $reqPendientes
          Requerimientos FINALIZADOS:         $reqFinalizados
        +-------------------------------------------------------+
        """.trimIndent()
    }

    // 4. Forzar Error
    fun forzarError() {
        try {
            // Simulamos un crash en tiempo de ejecución
            throw RuntimeException("Conexión de base de datos rechazada (Simulación)")
        } catch (e: Exception) {
            println(e);
            mensajeErrorSimulado = "✅ Excepción atrapada correctamente:\n${e.message}"
        }
    }
}