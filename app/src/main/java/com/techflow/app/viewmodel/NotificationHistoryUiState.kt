package com.techflow.app.viewmodel

import com.techflow.app.data.local.NotificationEntity

// NotificationHistoryUiState - estado de la pantalla de historial de notificaciones
// El ViewModel expone este estado con StateFlow y la vista lo observa con collectAsState()
data class NotificationHistoryUiState(
    // Lista de notificaciones guardadas localmente, ordenadas por fecha (más reciente primero)
    val notifications: List<NotificationEntity> = emptyList(),
    // true mientras se cargan las notificaciones desde Room (muestra indicador de carga)
    val isLoading: Boolean = true
)
