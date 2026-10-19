package com.example.sistemainventario

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Usuario(val user: String, val pass: String)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val usuariosRegistrados = mutableStateListOf(
        Usuario("admin", "1234")
    )

    var usuarioActual = mutableStateOf<Usuario?>(null)
        private set

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun registrar(user: String, pass: String): Boolean {
        if (user.isBlank() || pass.isBlank()) return false
        if (usuariosRegistrados.any { it.user == user }) {
            return false
        }
        usuariosRegistrados.add(Usuario(user, pass))
        return true
    }

    fun login(user: String, pass: String): Boolean {
        if (user.isBlank() || pass.isBlank()) {
            _authState.value = AuthState(false, "Campos vacíos")
            return false
        }
        val encontrado = usuariosRegistrados.find { it.user == user && it.pass == pass }
        if (encontrado != null) {
            usuarioActual.value = encontrado
            _authState.value = AuthState(true, null)
            return true
        }
        _authState.value = AuthState(false, "Credenciales incorrectas")
        return false
    }

    fun logout() {
        usuarioActual.value = null
        _authState.value = AuthState(false, null)
    }
}
