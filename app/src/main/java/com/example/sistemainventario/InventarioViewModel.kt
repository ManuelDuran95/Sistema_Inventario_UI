package com.example.sistemainventario

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class ActivoUiModel(
    val inventario: String,
    val nombre: String,
    val cc: String,
    val tipo: String
)

class InventarioViewModel : ViewModel() {
    private val _listaActivos = mutableStateListOf<ActivoUiModel>()
    val listaActivos: List<ActivoUiModel> = _listaActivos

    init {
        // Datos predeterminados para que la lista no empiece vacía
        _listaActivos.add(ActivoUiModel("PC-001", "Laptop Core i7", "CC01", "PC"))
        _listaActivos.add(ActivoUiModel("IMP-001", "Impresora Láser", "CC01", "IMPRESORA"))
    }

    fun registrarActivo(inventario: String, nombre: String, cc: String, tipo: String) {
        _listaActivos.add(ActivoUiModel(inventario, nombre, cc, tipo))
    }
}