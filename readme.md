# Sistema de Inventario - Android

Esta es la versión móvil (Android) del Sistema de Inventario, migrada desde su versión de consola original en Kotlin. La aplicación está construida utilizando una arquitectura moderna con **Jetpack Compose** para la interfaz de usuario.

## 🚀 Características Implementadas (Fase 1)

* **Sistema de Autenticación en Memoria (Sin persistencia):**
  * **Login:** Interfaz de inicio de sesión validada. Incluye un usuario administrador por defecto para pruebas rápidas (`Usuario: admin`, `Contraseña: 1234`).
  * **Registro:** Creación de nuevos usuarios durante el ciclo de vida activo de la aplicación.
  * **Cierre de Sesión:** Funcionalidad integrada en la barra superior de navegación.
* **Menú Principal (Dashboard):**
  * Interfaz de usuario basada en una cuadrícula (Grid).
  * Tarjetas de acceso rápido para las 6 operaciones principales (Listar, Registrar, Levantar Requerimiento, Actualizar Requerimiento, Reportes, Forzar Error).
* **Navegación:** Enrutamiento entre pantallas protegido.

## 🛠️ Tecnologías y Dependencias

El proyecto está desarrollado en **Kotlin** y utiliza las siguientes librerías principales de Android:

* **Jetpack Compose:** Herramienta principal para la construcción declarativa de la interfaz de usuario (Material Design 3).
* **Navigation Compose:** Manejo de rutas y pila de navegación entre pantallas.
  ```kotlin
  implementation("androidx.navigation:navigation-compose:2.7.7")

## Investigacion sobre Jetpack Compose
[Ver documento](Investigacion%20Jetpack%20Compose%20-%20Sistema%20de%20Inventario.pdf)