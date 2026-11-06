package com.example.sistemainventario

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(
        AuthState(
            isAuthenticated = auth.currentUser != null,
            email = auth.currentUser?.email
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _authState.update {
            it.copy(
                isAuthenticated = user != null,
                email = user?.email,
                isLoading = false,
                errorMessage = if (user != null) null else it.errorMessage
            )
        }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    fun limpiarError() {
        _authState.update { it.copy(errorMessage = null) }
    }

    fun registrar(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.update { it.copy(errorMessage = "Completa los campos") }
            return
        }
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            } catch (e: Exception) {
                _authState.update {
                    it.copy(isLoading = false, errorMessage = mapAuthError(e))
                }
            }
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.update { it.copy(errorMessage = "Campos vacíos") }
            return
        }
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                auth.signInWithEmailAndPassword(email.trim(), pass).await()
            } catch (e: Exception) {
                _authState.update {
                    it.copy(isLoading = false, errorMessage = mapAuthError(e))
                }
            }
        }
    }

    fun loginConGoogle(context: Context) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val idToken = GoogleSignInHelper.obtenerIdToken(context)
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            } catch (e: GoogleSignInCancelado) {
                _authState.update { it.copy(isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                _authState.update {
                    it.copy(isLoading = false, errorMessage = mapAuthError(e))
                }
            }
        }
    }

    fun logout(context: Context? = null) {
        auth.signOut()
        _authState.value = AuthState(isAuthenticated = false)
        if (context != null) {
            val appContext = context.applicationContext
            viewModelScope.launch { GoogleSignInHelper.limpiarCredenciales(appContext) }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    private fun mapAuthError(e: Exception): String {
        val code = (e as? FirebaseAuthException)?.errorCode
        return when (code) {
            "ERROR_INVALID_EMAIL" -> "Correo inválido"
            "ERROR_WRONG_PASSWORD",
            "ERROR_USER_NOT_FOUND",
            "ERROR_INVALID_CREDENTIAL" -> "Credenciales incorrectas"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está registrado"
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Sin conexión. Revisa tu internet"
            else -> e.localizedMessage ?: "No se pudo autenticar"
        }
    }
}
