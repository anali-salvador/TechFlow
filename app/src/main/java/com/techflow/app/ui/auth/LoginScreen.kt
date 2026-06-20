package com.techflow.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.theme.BackgroundLight
import com.techflow.app.ui.theme.OnSurfaceLight
import com.techflow.app.ui.theme.OnSurfaceVariantLight
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SurfaceLight
import com.techflow.app.viewmodel.AuthViewModel

// LoginScreen - Pantalla 1 del expediente técnico (Parte 2 - Autenticación)
// Permite iniciar sesión con correo y contraseña a través de Firebase Authentication (RF02)
// Si ya hay una sesión activa o el login es exitoso, navega al inventario (RF03)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estados locales del formulario, independientes del UiState del ViewModel
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Controla si la contraseña se muestra en texto plano o oculta con puntos
    var passwordVisible by remember { mutableStateOf(false) }
    // Controla si ya se intentó enviar el formulario, para mostrar errores de validación
    var showErrors by remember { mutableStateOf(false) }

    // Validación de formato de correo: debe contener "@" y "."
    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = password.isNotBlank()

    // RF03 (Persistencia de sesión) - cuando isAuthenticated pasa a true (login exitoso
    // o sesión ya activa detectada por el ViewModel), navega automáticamente al inventario
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onLoginSuccess()
        }
    }

    // Colores de los campos siguiendo la identidad visual PrimaryBlue de la app (ProductFormScreen)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = PrimaryBlue.copy(alpha = 0.5f),
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = OnSurfaceVariantLight,
        cursorColor = PrimaryBlue,
        focusedTextColor = OnSurfaceLight,
        unfocusedTextColor = OnSurfaceLight,
        focusedContainerColor = SurfaceLight,
        unfocusedContainerColor = SurfaceLight
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo - círculo grande con el icono representativo de inventario
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(PrimaryBlue.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Título de la app
            Text(
                text = "TechFlow",
                color = PrimaryBlue,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtítulo descriptivo
            Text(
                text = "Gestión de Inventario Tecnológico",
                color = OnSurfaceVariantLight,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo Correo - valida que contenga "@" y "."
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                isError = showErrors && !isEmailValid,
                supportingText = if (showErrors && !isEmailValid) {
                    { Text("Ingresa un correo válido") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Contraseña - con botón para mostrar/ocultar el texto ingresado
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = showErrors && !isPasswordValid,
                supportingText = if (showErrors && !isPasswordValid) {
                    { Text("La contraseña es obligatoria") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            // RF05 (Errores de autenticación) - mensaje visible si el login falla
            // (credenciales incorrectas, correo inválido, sin conexión, etc.)
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Iniciar sesión - valida el formulario y llama a viewModel.login()
            Button(
                onClick = {
                    showErrors = true
                    if (isEmailValid && isPasswordValid) {
                        viewModel.login(email.trim(), password)
                    }
                },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Iniciar sesión", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Acceso a la pantalla de Registro para usuarios nuevos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes cuenta?",
                    color = OnSurfaceVariantLight,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Regístrate",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
