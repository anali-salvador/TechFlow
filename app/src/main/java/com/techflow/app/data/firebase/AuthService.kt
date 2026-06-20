package com.techflow.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// AuthService - encapsula las llamadas a Firebase Authentication
// El Repository usa este servicio, nunca el ViewModel directamente
@Singleton
class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    // Retorna el usuario actual si hay sesión activa, null si no hay sesión
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Registra un nuevo usuario con correo y contraseña
    suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("No se pudo crear el usuario")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Inicia sesión con correo y contraseña existentes
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("No se pudo iniciar sesión")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cierra la sesión del usuario actual
    fun logout() {
        firebaseAuth.signOut()
    }
}
