package com.techflow.app.data.repository

import com.google.firebase.auth.FirebaseUser
import com.techflow.app.data.firebase.AuthService
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton - Hilt crea UNA sola instancia de este Repository para toda la app
// @Inject constructor - Hilt inyecta automáticamente el AuthService que necesitamos
// AuthRepository es la ÚNICA fuente de datos de autenticación que los ViewModels pueden usar
// Igual que ProductRepository es la única fuente de datos de productos, ningún ViewModel
// debe acceder directamente a AuthService o a FirebaseAuth
@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {

    // Retorna el usuario actual si hay sesión activa, null si no hay sesión
    fun getCurrentUser(): FirebaseUser? {
        return authService.getCurrentUser()
    }

    // Registra un nuevo usuario con correo y contraseña
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return authService.register(email, password)
    }

    // Inicia sesión con correo y contraseña existentes
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return authService.login(email, password)
    }

    // Cierra la sesión del usuario actual
    fun logout() {
        authService.logout()
    }
}
