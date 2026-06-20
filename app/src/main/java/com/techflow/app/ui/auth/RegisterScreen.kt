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
import com.techflow.app.ui.theme.OnSurfaceLight
import com.techflow.app.ui.theme.OnSurfaceVariantLight
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SurfaceLight
import com.techflow.app.viewmodel.AuthViewModel

// RegisterScreen - Pantalla 2 del expediente técnico (Parte 2 - Autenticación)
// Permite crear una cuenta nueva con correo y contraseña en Firebase Authentication (RF01)
// Mismo estilo visual que LoginScreen (fondo, fieldColors, logo) para mantener identidad de marca
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estados locales del formulario, independientes del UiState del ViewModel
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    // Controla si cada campo de contraseña se muestra en texto plano o oculto
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    // Controla si ya se intentó enviar el formulario, para mostrar errores de validación
    var showErrors by remember { mutableStateOf(false) }

    // Validación de formato de correo: debe contener "@" y "."
    val isEmailValid = email.contains("@") && email.contains(".")
    // La contraseña debe tener al menos 6 caracteres (mínimo exigido por Firebase Authentication)
    val isPasswordValid = password.length >= 6
    // Las dos contraseñas ingresadas deben coincidir
    val doPasswordsMatch = password == confirmPassword

    // RF01 (Registro) - cuando isAuthenticated pasa a true tras un registro exitoso,
    // navega automáticamente al inventario
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onRegisterSuccess()
        }
    }

    // Mismo estilo de campos PrimaryBlue que LoginScreen, para mantener consistencia visual
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

            // Logo - mismo círculo grande con el icono representativo de inventario
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

            // Campo Contraseña - mínimo 6 caracteres, con botón para mostrar/ocultar
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
                    { Text("La contraseña debe tener al menos 6 caracteres") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Confirmar Contraseña - debe coincidir exactamente con la contraseña ingresada
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = showErrors && !doPasswordsMatch,
                supportingText = if (showErrors && !doPasswordsMatch) {
                    { Text("Las contraseñas no coinciden") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            // RF05 (Errores de autenticación) - mensaje visible si el registro falla
            // (correo ya registrado, correo inválido, contraseña débil, etc.)
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrarse - solo llama a viewModel.register() si todo el formulario es válido
            Button(
                onClick = {
                    showErrors = true
                    if (isEmailValid && isPasswordValid && doPasswordsMatch) {
                        viewModel.register(email.trim(), password)
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
                    Text(text = "Registrarse", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Acceso de vuelta a la pantalla de Login para usuarios que ya tienen cuenta
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    color = OnSurfaceVariantLight,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Inicia sesión",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
