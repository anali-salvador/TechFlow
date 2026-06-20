package com.techflow.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// NotificationDao - operaciones sobre el historial local de notificaciones
@Dao
interface NotificationDao {

    // @Insert - guarda un nuevo registro en el historial cada vez que se muestra una notificación
    @Insert
    suspend fun insertNotification(notification: NotificationEntity)

    // Flow - la pantalla de historial se actualiza automáticamente al llegar una notificación nueva
    @Query("SELECT * FROM notificaciones ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    // Marca una notificación como leída cuando el usuario la abre desde el historial
    @Query("UPDATE notificaciones SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    // Limpia todo el historial de notificaciones
    @Query("DELETE FROM notificaciones")
    suspend fun deleteAllNotifications()
}
