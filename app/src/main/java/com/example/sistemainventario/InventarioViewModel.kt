package com.example.sistemainventario

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActivoUiModel(
    val inventario: String,
    val nombre: String,
    val cc: String,
    val tipo: String
)

class InventarioViewModel : ViewModel() {
    private val _listaActivos = mutableStateListOf<ActivoUiModel>()
    val listaActivos: List<ActivoUiModel> = _listaActivos

    // Architectural StateFlow (Point 5)
    private val _activosFlow = MutableStateFlow<List<ActivoUiModel>>(emptyList())
    val activosFlow: StateFlow<List<ActivoUiModel>> = _activosFlow.asStateFlow()

    init {
        // Datos predeterminados para que la lista no empiece vacía
        _listaActivos.add(ActivoUiModel("PC-001", "Laptop Core i7", "CC01", "PC"))
        _listaActivos.add(ActivoUiModel("IMP-001", "Impresora Láser", "CC01", "IMPRESORA"))
        _activosFlow.value = _listaActivos.toList()
    }

    fun registrarActivo(inventario: String, nombre: String, cc: String, tipo: String): Boolean {
        if (inventario.isBlank() || nombre.isBlank()) return false
        if (_listaActivos.any { it.inventario.equals(inventario, ignoreCase = true) }) {
            return false
        }
        val nuevo = ActivoUiModel(inventario.trim().uppercase(), nombre.trim(), cc, tipo)
        _listaActivos.add(nuevo)
        _activosFlow.value = _listaActivos.toList()
        return true
    }
}
