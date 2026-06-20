package com.techflow.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// NotificationEntity - historial local de notificaciones (funcionalidad extra)
// Guarda un registro cada vez que se muestra una notificación de stock bajo (RF16)
// o llega una notificación push desde Firebase Cloud Messaging (RF18)
@Entity(tableName = "notificaciones")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String,
    // productId - permite navegar al detalle del producto afectado al tocar el historial
    // null cuando la notificación no está asociada a un producto (ej. push genérico)
    val productId: Int?,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
