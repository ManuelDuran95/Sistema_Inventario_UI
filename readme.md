# Sistema de Inventario - Android

Esta es la versión móvil (Android) del Sistema de Inventario, migrada desde su versión de consola original en Kotlin. La aplicación está construida con **Jetpack Compose** para la interfaz de usuario y usa **Firebase** (Authentication y Cloud Firestore) como backend en la nube.

## ☁️ Avance 2: Integración con Firebase

### Autenticación con Firebase
* **Correo electrónico y contraseña:** registro e inicio de sesión con Firebase Authentication, con mensajes de error en español (correo inválido, contraseña débil, correo ya registrado, sin conexión).
* **Cuenta de Google:** inicio de sesión con Google mediante Credential Manager. El token de Google se intercambia por una credencial de Firebase.
* **Sesión persistente:** al volver a abrir la app, el usuario autenticado entra directamente al menú.
* **Cierre de sesión:** cierra la sesión de Firebase y limpia la cuenta de Google seleccionada.

### Persistencia en Cloud Firestore
Los datos se guardan en Firestore y se sincronizan en tiempo real entre dispositivos y usuarios. Cada registro queda asociado al usuario autenticado que lo creó o modificó.

| Colección | Contenido | Asociación con el usuario |
|---|---|---|
| `users/{uid}` | Perfil del usuario (correo, último acceso) | Documento con el UID del usuario |
| `activos/{codigo}` | Equipos del inventario | `registradoPor`, `registradoPorUid`, `creadoEn` |
| `requerimientos/{id}` | Tickets de soporte | `creadoPor`, `creadoPorUid`, `creadoEn`, `finalizadoPor`, `finalizadoPorUid`, `finalizadoEn` |
| `contadores/requerimientos` | Último número de ticket | Numeración consecutiva sin duplicados (transacción) |

Las **reglas de seguridad** ([firestore.rules](firestore.rules)) exigen sesión iniciada, obligan a que el UID guardado en cada registro sea el del usuario autenticado, solo permiten modificar el estado de un ticket al finalizarlo y no permiten borrar registros.

### Módulos de la app
* **Listar Activos:** inventario compartido con búsqueda por código, nombre o tipo; muestra quién registró cada equipo.
* **Registrar Activo:** alta de equipos con validación de código duplicado.
* **Levantar Requerimiento:** crea un ticket para un activo existente; el formulario se limpia al guardar para evitar tickets duplicados.
* **Actualizar Requerimiento:** lista de tickets pendientes y resumen del ticket (activo, falla, solicitante, estado) antes de finalizarlo.
* **Historial:** todos los tickets almacenados con filtros (Todos, Mis tickets, Pendientes, Finalizados), contadores y la trazabilidad de quién creó y finalizó cada ticket y cuándo.
* **Ver Reporte:** resumen consolidado de equipos por tipo y tickets por estado.
* **Forzar Error:** prueba del manejo de excepciones.

### Configuración de Firebase
El archivo `app/google-services.json` del proyecto `sistema-inventario-39bf8` ya está incluido en el repositorio, por lo que el login con correo y Firestore funcionan al clonar y compilar.

Para que el **login con Google** funcione en otra computadora, hay que registrar la huella SHA-1 de su keystore de depuración:
1. Obtener la huella con `./gradlew signingReport` (variante `debug`, campo `SHA1`).
2. En la consola de Firebase: **Configuración del proyecto → Tus apps → Android → Agregar huella digital** y pegar el SHA-1.
3. Descargar de nuevo `google-services.json` y reemplazar el de `app/`.

Configuración ya realizada en la consola: *Correo electrónico/contraseña* y *Google* habilitados en **Authentication**, y reglas de [firestore.rules](firestore.rules) publicadas en **Firestore**.

## 🚀 Avance 1: Interfaz y navegación

* **Menú principal (Dashboard):** cuadrícula con tarjetas de acceso rápido a las operaciones principales.
* **Navegación:** enrutamiento entre pantallas protegido; si no hay sesión, la app regresa al login.
* En esta fase la autenticación era en memoria (sin persistencia); en el Avance 2 se reemplazó por Firebase Authentication.

## 🛠️ Tecnologías y dependencias

* **Kotlin** y **Jetpack Compose** (Material Design 3).
* **Navigation Compose:** rutas y pila de navegación.
* **Firebase BoM:** `firebase-auth` y `firebase-firestore`.
* **Credential Manager** y **Google Identity** (`googleid`): inicio de sesión con Google.
* **Kotlin Coroutines** (`kotlinx-coroutines-play-services`): llamadas asíncronas a Firebase con `await()`.
