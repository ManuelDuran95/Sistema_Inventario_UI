package com.example.sistemainventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sistemainventario.ui.theme.SistemaDeInventarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaDeInventarioTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {

                    composable("login") {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginExitoso = {
                                navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                                }
                            },
                            onIrARegistro = { navController.navigate("registro") }
                        )
                    }

                    composable("registro") {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            onRegistroExitoso = { navController.popBackStack() },
                            onVolverLogin = { navController.popBackStack() }
                        )
                    }

                    composable("main") {
                        MainScreen(
                            authViewModel = authViewModel,
                            onLogout = {
                                // Al salir, volvemos al login y borramos el historial
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}