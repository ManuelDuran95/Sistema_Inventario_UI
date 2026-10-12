package com.example.sistemainventario

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Usuario(val user: String, val pass: String)

class AuthViewModel : ViewModel() {
    private val usuariosRegistrados = mutableStateListOf(
        Usuario("admin", "1234")
    )

    var usuarioActual = mutableStateOf<Usuario?>(null)
        private set

    fun registrar(user: String, pass: String): Boolean {
        if (usuariosRegistrados.any { it.user == user }) {
            return false
        }
        usuariosRegistrados.add(Usuario(user, pass))
        return true
    }

    fun login(user: String, pass: String): Boolean {
        val encontrado = usuariosRegistrados.find { it.user == user && it.pass == pass }
        if (encontrado != null) {
            usuarioActual.value = encontrado
            return true
        }
        return false
    }

    fun logout() {
        usuarioActual.value = null
    }
}