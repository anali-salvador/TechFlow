package com.techflow.app.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.techflow.app.MainActivity
import com.techflow.app.data.local.NotificationDao
import com.techflow.app.data.local.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

// TechFlowMessagingService - cumple RF18 del expediente técnico
// "La app puede recibir notificaciones push desde la consola de Firebase"
// FCM entrega los mensajes a este servicio automáticamente, tanto con la app en primer plano
// como en segundo plano (o incluso cerrada), sin que se necesite ningún código adicional
// para escuchar o hacer polling: el sistema operativo despierta el servicio cuando llega un mensaje
// @AndroidEntryPoint - permite que Hilt inyecte el NotificationDao en este servicio,
// necesario para guardar cada push recibido en el historial de notificaciones (funcionalidad extra)
@AndroidEntryPoint
class TechFlowMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    // Scope propio para guardar en Room sin bloquear onMessageReceived (que no es una función suspend)
    // Se cancela en onDestroy() para no dejar corrutinas huérfanas si el servicio se destruye
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private companion object {
        const val TAG = "TechFlowMessagingService"
    }

    // onMessageReceived - se ejecuta cuando llega un mensaje push desde la consola de Firebase
    // Si la app está en segundo plano y el mensaje tiene un bloque "notification", el sistema
    // ya muestra la notificación por defecto; aquí la mostramos manualmente para tener control
    // total del canal, el ícono y el Intent, y para que funcione igual en ambos casos (RF18)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtiene el título y cuerpo del mensaje configurados en la consola de Firebase
        val title = remoteMessage.notification?.title ?: "TechFlow"
        val body = remoteMessage.notification?.body ?: ""

        // Intent hacia MainActivity - al tocar la notificación push, abre la app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent.getActivity - FLAG_IMMUTABLE obligatorio desde Android 12
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Usa el mismo canal de notificaciones que NotificationHelper (CHANNEL_ID)
        // Ese canal ya fue creado en TechFlowApp.onCreate() al iniciar la app
        val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
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
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                return
            }
        }

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)

        // Guarda el registro en el historial local de notificaciones (funcionalidad extra)
        // productId = null porque un push de Firebase no necesariamente está ligado a un producto
        serviceScope.launch {
            notificationDao.insertNotification(
                NotificationEntity(
                    title = title,
                    body = body,
                    productId = null
                )
            )
        }
    }

    // onNewToken - FCM llama esto cuando genera o renueva el token del dispositivo
    // El expediente técnico no requiere un backend propio, así que solo se deja constancia
    // en el log para referencia; no es necesario enviarlo a ningún servidor
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token de FCM: $token")
    }

    // Cancela el scope propio al destruirse el servicio, evitando fugas de corrutinas
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}