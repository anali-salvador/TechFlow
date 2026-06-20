package com.techflow.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.techflow.app.MainActivity
import com.techflow.app.data.local.NotificationDao
import com.techflow.app.data.local.NotificationEntity
import javax.inject.Inject
import javax.inject.Singleton

// NotificationHelper - cumple RF16/RF17 del expediente técnico
// RF16: el sistema envía una notificación local cuando un producto llega al stock mínimo
// RF17: al tocar la notificación, la app abre la pantalla del producto afectado
// Ahora es una clase inyectable por Hilt (antes era un object) porque necesita el
// NotificationDao para guardar cada notificación en el historial local (funcionalidad extra)
@Singleton
class NotificationHelper @Inject constructor(
    private val notificationDao: NotificationDao
) {

    companion object {
        // ID del canal de notificaciones - debe ser único y estable entre versiones de la app
        const val CHANNEL_ID = "stock_alerts_channel"

        // Nombre visible del canal en la configuración de notificaciones del sistema
        const val CHANNEL_NAME = "Alertas de Stock"

        // Crea el canal de notificación, obligatorio desde Android 8 (API 26) en adelante
        // Se mantiene como función estática porque solo necesita el Context (no el DAO) y se
        // llama una sola vez en TechFlowApp.onCreate(), antes de que Hilt pueda inyectar nada
        // IMPORTANCE_HIGH - hace que la alerta de stock bajo aparezca como heads-up notification
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones cuando un producto del inventario llega al stock mínimo"
                }
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // Muestra la notificación de stock bajo para un producto específico (RF16)
    // productId - se usa tanto para abrir el detalle correcto (RF17) como para el ID único de la notificación
    // suspend - además de mostrar la notificación del sistema, guarda un registro en el
    // historial local (NotificationEntity) para la pantalla de historial de notificaciones
    suspend fun showLowStockNotification(context: Context, productId: Int, productName: String, cantidad: Int) {

        val title = "Stock bajo: $productName"
        val body = "Quedan $cantidad unidades. Toca para ver el detalle."

        // RF17 - Intent hacia MainActivity con el ID del producto como extra
        // Al abrir la app desde la notificación, se puede leer "productId" y navegar al detalle
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("productId", productId)
        }

        // PendingIntent.getActivity - permite que el sistema lance el Intent al tocar la notificación
        // FLAG_UPDATE_CURRENT - si ya existe un PendingIntent igual, actualiza sus extras en vez de duplicarlo
        // FLAG_IMMUTABLE - obligatorio desde Android 12, el sistema no puede modificar este PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            productId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construye la notificación con el canal de alertas de stock
        // android.R.drawable.ic_dialog_alert - ícono del sistema, no hay un ícono vectorial propio del proyecto
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Desde Android 13 (API 33) se requiere el permiso POST_NOTIFICATIONS en tiempo de ejecución
        // Si el usuario no lo concedió, no se notifica (no se puede mostrar sin el permiso)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return
            }
        }

        // ID único basado en productId - así cada producto tiene su propia notificación
        // y no se sobrescriben entre sí si hay varios productos con stock bajo a la vez
        NotificationManagerCompat.from(context).notify(productId, notification)

        // Guarda el registro en el historial local de notificaciones (funcionalidad extra)
        notificationDao.insertNotification(
            NotificationEntity(
                title = title,
                body = body,
                productId = productId
            )
        )
    }
}
