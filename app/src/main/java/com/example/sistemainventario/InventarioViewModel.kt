package com.example.sistemainventario

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Modelos de datos para la UI (Simples y directos)
data class ActivoUiModel(
    val inventario: String,
    val nombre: String,
    val cc: String,
    val tipo: String,
    val registradoPor: String = "",
    val creadoEn: Long? = null
)

data class RequerimientoUiModel(
    val id: Int,
    val docId: String = "",
    val inventario: String,
    val falla: String,
    val usuario: String,
    val estado: String = "PENDIENTE",
    val creadoPor: String = "",
    val creadoPorUid: String = "",
    val creadoEn: Long? = null,
    val finalizadoPor: String = "",
    val finalizadoEn: Long? = null
)

class InventarioViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ==========================================
    // ESTADO DE DATOS (sincronizado con Firestore)
    // ==========================================
    private val _listaActivos = mutableStateListOf<ActivoUiModel>()
    val listaActivos: List<ActivoUiModel> = _listaActivos

    private val _listaRequerimientos = mutableStateListOf<RequerimientoUiModel>()
    val listaRequerimientos: List<RequerimientoUiModel> = _listaRequerimientos

    // ==========================================
    // ESTADO DE LA INTERFAZ (Mensajes para la UI)
    // ==========================================
    var mensajeReq by mutableStateOf("")
    var guardandoReq by mutableStateOf(false)
        private set
    var mensajeUpdate by mutableStateOf("")
    var finalizandoReq by mutableStateOf(false)
        private set
    var textoReporte by mutableStateOf("")
    var mensajeErrorSimulado by mutableStateOf("")

    val correoUsuario: String
        get() = auth.currentUser?.email.orEmpty()

    val uidUsuario: String
        get() = auth.currentUser?.uid.orEmpty()

    private var activosReg: ListenerRegistration? = null
    private var reqsReg: ListenerRegistration? = null
    private var migrando = false

    private val activosCol get() = db.collection("activos")
    private val reqsCol get() = db.collection("requerimientos")
    private val contadorReqs get() = db.collection("contadores").document("requerimientos")

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            escucharDatos()
            viewModelScope.launch { asegurarPerfilYMigrar(user.uid, user.email.orEmpty()) }
        } else {
            detenerYLimpiar()
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    private fun userDoc(uid: String) = db.collection("users").document(uid)

    private fun escucharDatos() {
        detenerListeners()

        activosReg = activosCol.addSnapshotListener { snap, _ ->
            if (snap == null) return@addSnapshotListener
            _listaActivos.clear()
            _listaActivos.addAll(
                snap.documents.map { doc ->
                    ActivoUiModel(
                        inventario = doc.getString("inventario") ?: doc.id,
                        nombre = doc.getString("nombre").orEmpty(),
                        cc = doc.getString("cc").orEmpty(),
                        tipo = doc.getString("tipo").orEmpty(),
                        registradoPor = doc.getString("registradoPor").orEmpty(),
                        creadoEn = doc.getLong("creadoEn")
                    )
                }
            )
        }

        reqsReg = reqsCol.addSnapshotListener { snap, _ ->
            if (snap == null) return@addSnapshotListener
            _listaRequerimientos.clear()
            _listaRequerimientos.addAll(
                snap.documents.map { doc ->
                    RequerimientoUiModel(
                        id = (doc.getLong("id") ?: 0L).toInt(),
                        docId = doc.id,
                        inventario = doc.getString("inventario").orEmpty(),
                        falla = doc.getString("falla").orEmpty(),
                        usuario = doc.getString("usuario").orEmpty(),
                        estado = doc.getString("estado") ?: "PENDIENTE",
                        creadoPor = doc.getString("creadoPor").orEmpty(),
                        creadoPorUid = doc.getString("creadoPorUid").orEmpty(),
                        creadoEn = doc.getLong("creadoEn"),
                        finalizadoPor = doc.getString("finalizadoPor").orEmpty(),
                        finalizadoEn = doc.getLong("finalizadoEn")
                    )
                }
            )
        }
    }

    private suspend fun asegurarPerfilYMigrar(uid: String, email: String) {
        if (migrando) return
        migrando = true
        try {
            val perfilRef = userDoc(uid)
            val perfil = perfilRef.get().await()
            if (perfil.getBoolean("migradoACompartido") == true) return

            val activosPersonales = perfilRef.collection("activos").get().await()
            for (doc in activosPersonales.documents) {
                val destino = activosCol.document(doc.id)
                if (!destino.get().await().exists()) {
                    destino.set(doc.data.orEmpty() + mapOf("registradoPor" to email, "registradoPorUid" to uid)).await()
                }
            }

            val fallasDeEjemplo = setOf("No enciende la pantalla", "Atasco de papel")
            val reqsPersonales = perfilRef.collection("requerimientos").get().await()
                .documents
                .filterNot { it.id.startsWith("seed-") || it.getString("falla") in fallasDeEjemplo }
                .sortedBy { it.getLong("id") ?: 0L }
            for (doc in reqsPersonales) {
                val nuevoId = siguienteIdRequerimiento()
                reqsCol.document(nuevoId.toString()).set(
                    doc.data.orEmpty() + mapOf("id" to nuevoId, "creadoPor" to email, "creadoPorUid" to uid)
                ).await()
            }

            perfilRef.set(
                mapOf("email" to email, "migradoACompartido" to true, "ultimoAcceso" to System.currentTimeMillis()),
                SetOptions.merge()
            ).await()
        } catch (_: Exception) {
        } finally {
            migrando = false
        }
    }

    private suspend fun siguienteIdRequerimiento(): Long =
        db.runTransaction { tx ->
            val actual = tx.get(contadorReqs).getLong("ultimo") ?: 0L
            val siguiente = actual + 1
            tx.set(contadorReqs, mapOf("ultimo" to siguiente))
            siguiente
        }.await()

    // ==========================================
    // MÉTODOS LÓGICOS DE LA APLICACIÓN
    // ==========================================

    fun registrarActivo(inventario: String, nombre: String, cc: String, tipo: String): Boolean {
        if (auth.currentUser == null) return false
        if (inventario.isBlank() || nombre.isBlank()) return false
        // Evitar duplicados
        if (_listaActivos.any { it.inventario.equals(inventario, ignoreCase = true) }) {
            return false
        }

        val codigo = inventario.trim().uppercase()
        val data = mapOf(
            "inventario" to codigo,
            "nombre" to nombre.trim(),
            "cc" to cc,
            "tipo" to tipo,
            "registradoPor" to correoUsuario,
            "registradoPorUid" to uidUsuario,
            "creadoEn" to System.currentTimeMillis()
        )
        viewModelScope.launch {
            try {
                activosCol.document(codigo).set(data).await()
            } catch (e: Exception) {
                mensajeErrorSimulado = "No se pudo guardar el activo: ${e.localizedMessage}"
            }
        }
        return true
    }

    // 1. Levantar Requerimiento
    fun levantarRequerimiento(cod: String, falla: String, usuario: String, onGuardado: () -> Unit) {
        if (guardandoReq) return
        if (auth.currentUser == null) {
            mensajeReq = "Error: Debes iniciar sesión."
            return
        }

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

        guardandoReq = true
        viewModelScope.launch {
            try {
                // Generar ID autoincrementable y guardar
                val nuevoId = siguienteIdRequerimiento()
                reqsCol.document(nuevoId.toString()).set(
                    mapOf(
                        "id" to nuevoId,
                        "inventario" to codTrim,
                        "falla" to falla.trim(),
                        "usuario" to usuario.trim(),
                        "estado" to "PENDIENTE",
                        "creadoPor" to correoUsuario,
                        "creadoPorUid" to uidUsuario,
                        "creadoEn" to System.currentTimeMillis()
                    )
                ).await()
                mensajeReq = "✅ Requerimiento #$nuevoId creado (PENDIENTE)."
                onGuardado()
            } catch (e: Exception) {
                mensajeReq = "Error: ${e.localizedMessage}"
            } finally {
                guardandoReq = false
            }
        }
    }

    fun buscarRequerimiento(idStr: String): RequerimientoUiModel? {
        val id = idStr.trim().toIntOrNull() ?: return null
        return _listaRequerimientos.find { it.id == id }
    }

    fun nombreActivo(inventario: String): String? =
        _listaActivos.find { it.inventario.equals(inventario, ignoreCase = true) }?.nombre

    // 2. Actualizar Estado
    fun actualizarRequerimiento(idStr: String) {
        if (finalizandoReq) return
        if (auth.currentUser == null) {
            mensajeUpdate = "❌ Debes iniciar sesión."
            return
        }

        // Buscar requerimiento por ID
        val req = buscarRequerimiento(idStr)
        if (req == null) {
            mensajeUpdate = "❌ Requerimiento no encontrado."
            return
        }
        if (req.estado == "FINALIZADO") {
            mensajeUpdate = "❌ El requerimiento #${req.id} ya está finalizado."
            return
        }

        finalizandoReq = true
        viewModelScope.launch {
            try {
                reqsCol.document(req.docId)
                    .update(
                        mapOf(
                            "estado" to "FINALIZADO",
                            "finalizadoEn" to System.currentTimeMillis(),
                            "finalizadoPor" to correoUsuario,
                            "finalizadoPorUid" to uidUsuario
                        )
                    )
                    .await()
                mensajeUpdate = "✅ Requerimiento #${req.id} cambiado a [FINALIZADO]."
            } catch (e: Exception) {
                mensajeUpdate = "❌ ${e.localizedMessage}"
            } finally {
                finalizandoReq = false
            }
        }
    }

    // 3. Ver Reporte
    fun cargarReporte() {
        // Calcular estadísticas
        val totalPcs = _listaActivos.count { it.tipo.equals("PC", ignoreCase = true) }
        val totalImpresoras = _listaActivos.count { it.tipo.equals("IMPRESORA", ignoreCase = true) }
        val otros = _listaActivos.size - totalPcs - totalImpresoras
        val reqPendientes = _listaRequerimientos.count { it.estado == "PENDIENTE" }
        val reqFinalizados = _listaRequerimientos.count { it.estado == "FINALIZADO" }

        textoReporte = """
        +-------------------------------------------------------+
        |             REPORTE CONSOLIDADO DEL SISTEMA           |
        +-------------------------------------------------------+
          Total Computadoras Registradas: $totalPcs
          Total Impresoras Registradas:   $totalImpresoras
          Otros equipos:                  $otros
          
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
            println(e)
            mensajeErrorSimulado = "✅ Excepción atrapada correctamente:\n${e.message}"
        }
    }

    private fun detenerListeners() {
        activosReg?.remove()
        reqsReg?.remove()
        activosReg = null
        reqsReg = null
    }

    private fun detenerYLimpiar() {
        detenerListeners()
        _listaActivos.clear()
        _listaRequerimientos.clear()
        mensajeReq = ""
        mensajeUpdate = ""
        textoReporte = ""
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        detenerListeners()
        super.onCleared()
    }
}
