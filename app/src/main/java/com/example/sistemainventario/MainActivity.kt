package com.example.sistemainventario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
                // 1. Inicializamos la memoria de tu inventario aquí para que no se borre al cambiar de pantalla
                val inventarioViewModel: InventarioViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()
                val startDestination = remember { if (authState.isAuthenticated) "main" else "login" }

                NavHost(navController = navController, startDestination = startDestination) {

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
                            onRegistroExitoso = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onVolverLogin = { navController.popBackStack() }
                        )
                    }

                    composable("main") {
                        MainScreen(
                            authViewModel = authViewModel,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("main") { inclusive = true }
                                }
                            },
                            navController = navController,
                            // 2. Conectamos los clics del menú hacia tus pantallas
                            onIrAListar = { navController.navigate("listar_activos") },
                            onIrARegistrar = { navController.navigate("registrar_activo") }
                        )
                    }

                    // 3. Tus dos nuevas pantallas registradas en la navegación
                    composable("listar_activos") {
                        ListarActivosScreen(
                            viewModel = inventarioViewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    composable("registrar_activo") {
                        RegistroActivoScreen(
                            viewModel = inventarioViewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }

                    composable("levantar_req") { LevantarReqScreen(inventarioViewModel) { navController.popBackStack() } }
                    composable("actualizar_req") { ActualizarReqScreen(inventarioViewModel) { navController.popBackStack() } }
                    composable("historial") { HistorialScreen(inventarioViewModel) { navController.popBackStack() } }
                    composable("reporte") { VerReporteScreen(inventarioViewModel) { navController.popBackStack() } }
                    composable("forzar_error") { ForzarErrorScreen(inventarioViewModel) { navController.popBackStack() } }
                }

                val backStack by navController.currentBackStackEntryAsState()
                val ruta = backStack?.destination?.route
                LaunchedEffect(authState.isAuthenticated, ruta) {
                    if (authState.isAuthenticated && ruta in listOf("login", "registro")) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else if (!authState.isAuthenticated && ruta != null && ruta != "login" && ruta != "registro") {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
