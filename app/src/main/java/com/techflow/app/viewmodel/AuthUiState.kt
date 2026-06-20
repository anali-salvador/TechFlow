package com.techflow.app.viewmodel

// data class UiState - contiene TODO el estado de las pantallas de autenticación (Login/Register)
// El ViewModel expone este estado con StateFlow y la vista lo observa con collectAsState()
// Cuando cualquier campo cambia, Compose hace recomposición automática de la UI
data class AuthUiState(
    // RF01/RF02 (Registro/Login) - true mientras se espera la respuesta de Firebase Authentication
    // Muestra el indicador de carga en el botón de "Registrarse" o "Iniciar sesión"
    val isLoading: Boolean = false,

    // RF05 (Errores de autenticación) - mensaje de error si el registro o login fallan
    // (correo ya registrado, contraseña incorrecta, credenciales inválidas, sin conexión, etc.)
    val errorMessage: String? = null,

    // RF03 (Persistencia de sesión) - true si hay un usuario con sesión activa en Firebase Auth
    // Se usa para decidir si la app navega a InventoryList o se queda en Login
    val isAuthenticated: Boolean = false,

    // RF03/RF04 (Persistencia de sesión / aislamiento de datos por usuario) - UID del usuario autenticado
    // Se usa como filtro userId en Room y como ruta en Firestore (usuarios/{userId}/productos)
    val userId: String = "",

    // RF01/RF02 (Registro/Login) - correo del usuario autenticado, se muestra en la UI si se requiere
    val userEmail: String = ""
)
