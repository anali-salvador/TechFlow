package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.techflow.app.util.extraerNombreDesdeCorreo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// ProfileViewModel - expone los datos de SOLO LECTURA del usuario autenticado
// Lee directamente de FirebaseAuth (mismo patrón que ya usa InventoryListScreen para
// mostrar el nombre del usuario), no modifica nada de la lógica de autenticación existente
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        _uiState.value = ProfileUiState(
            email = currentUser?.email ?: "",
            nombre = extraerNombreDesdeCorreo(currentUser?.email),
            miembroDesde = formatMiembroDesde(currentUser?.metadata?.creationTimestamp)
        )
    }

    private fun formatMiembroDesde(creationTimestamp: Long?): String {
        if (creationTimestamp == null || creationTimestamp == 0L) return "—"
        val formatter = SimpleDateFormat("d 'de' MMMM, yyyy", Locale("es", "PE"))
        return formatter.format(Date(creationTimestamp))
    }
}
