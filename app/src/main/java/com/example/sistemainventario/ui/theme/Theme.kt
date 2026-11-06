package com.example.sistemainventario.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Azul,
    onPrimary = Blanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = Verde,
    onSecondary = Blanco,
    secondaryContainer = VerdeClaro,
    onSecondaryContainer = Pizarra800,
    error = Rojo,
    onError = Blanco,
    errorContainer = RojoClaro,
    onErrorContainer = Rojo,
    background = Pizarra100,
    onBackground = Pizarra800,
    surface = Blanco,
    onSurface = Pizarra800,
    surfaceVariant = Pizarra100,
    onSurfaceVariant = Pizarra600,
    outline = Pizarra400,
    outlineVariant = Pizarra200
)

@Composable
fun SistemaDeInventarioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
