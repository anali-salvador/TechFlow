package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techflow.app.data.local.NotificationDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt inyecta el NotificationDao para leer y limpiar el historial local
// Funcionalidad extra: pantalla de historial de notificaciones (stock bajo + push de FCM)
@HiltViewModel
class NotificationHistoryViewModel @Inject constructor(
    private val notificationDao: NotificationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationHistoryUiState())
    val uiState: StateFlow<NotificationHistoryUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    // Observa el Flow de Room: cada vez que se guarda una notificación nueva
    // (desde NotificationHelper o TechFlowMessagingService), la lista se actualiza sola
    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            notificationDao.getAllNotifications().collect { notifications ->
                _uiState.value = _uiState.value.copy(
                    notifications = notifications,
                    isLoading = false
                )
            }
        }
    }

    // Borra todo el historial de notificaciones (botón de la papelera en el TopAppBar)
    fun clearAll() {
        viewModelScope.launch {
            notificationDao.deleteAllNotifications()
        }
    }
}
