package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techflow.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt se encarga de crear este ViewModel e inyectar el AuthRepository
// El ViewModel sobrevive a cambios de configuración (rotación de pantalla)
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // _uiState es privado y mutable, solo el ViewModel puede modificarlo
    // uiState es público e inmutable, la vista solo puede leerlo (observarlo)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // RF03 (Persistencia de sesión) - al crear el ViewModel se revisa si Firebase Auth
    // ya tiene un usuario logueado (sesión persistida entre cierres de la app)
    // Si existe, se actualiza el UiState para que la app navegue directo al inventario
    // sin pedir login de nuevo
    init {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                userId = currentUser.uid,
                userEmail = currentUser.email ?: ""
            )
        }
    }

    // RF01 (Registro) - crea una cuenta nueva con correo y contraseña en Firebase Auth
    // Mientras se espera la respuesta se muestra isLoading=true en el botón de "Registrarse"
    // Si tiene éxito se marca isAuthenticated=true con los datos del usuario creado
    // Si falla se traduce el error técnico de Firebase a un mensaje amigable en español
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userId = user.uid,
                        userEmail = user.email ?: ""
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = mapRegisterError(e.message)
                    )
                }
            )
        }
    }

    // RF02 (Login) - inicia sesión con correo y contraseña existentes en Firebase Auth
    // Mientras se espera la respuesta se muestra isLoading=true en el botón de "Iniciar sesión"
    // Si tiene éxito se marca isAuthenticated=true con los datos del usuario
    // Si falla se traduce el error técnico de Firebase a un mensaje amigable en español
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userId = user.uid,
                        userEmail = user.email ?: ""
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = mapLoginError(e.message)
                    )
                }
            )
        }
    }

    // RF03 (Persistencia de sesión) - cierra la sesión en Firebase Auth y limpia el UiState
    // Al volver a AuthUiState() vacío, isAuthenticated queda en false y la app vuelve a Login
    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    // RF05 (Errores de autenticación) - limpia el mensaje de error después de mostrarlo
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // RF05 (Errores de autenticación) - traduce los mensajes técnicos de Firebase Auth
    // que llegan al registrar una cuenta a mensajes amigables en español para el usuario
    private fun mapRegisterError(message: String?): String {
        val msg = message ?: ""
        return when {
            msg.contains("email address is already in use", ignoreCase = true) ->
                "Este correo ya está registrado"
            msg.contains("badly formatted", ignoreCase = true) ->
                "Correo inválido"
            msg.contains("password is invalid", ignoreCase = true) ||
                msg.contains("weak-password", ignoreCase = true) ->
                "La contraseña debe tener al menos 6 caracteres"
            else -> "Error al registrarse"
        }
    }

    // RF05 (Errores de autenticación) - traduce los mensajes técnicos de Firebase Auth
    // que llegan al iniciar sesión a mensajes amigables en español para el usuario
    private fun mapLoginError(message: String?): String {
        val msg = message ?: ""
        return when {
            msg.contains("no user record", ignoreCase = true) ||
                msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
                "Correo o contraseña incorrectos"
            msg.contains("badly formatted", ignoreCase = true) ->
                "Correo inválido"
            else -> "Error al iniciar sesión"
        }
    }
}
