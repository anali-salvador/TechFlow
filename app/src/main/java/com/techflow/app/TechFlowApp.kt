package com.techflow.app

import android.app.Application
import com.techflow.app.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp - activa Hilt en toda la aplicación
// Esta anotación genera el código necesario para que Hilt pueda inyectar dependencias
// Sin esta clase, Hilt no funciona y la app crashea al iniciar
// Se debe registrar en el AndroidManifest.xml con android:name=".TechFlowApp"
@HiltAndroidApp
class TechFlowApp : Application() {

    // RF16 - el canal de notificaciones debe existir antes de mostrar cualquier alerta de stock bajo
    // Se crea una sola vez al iniciar la app (crear un canal ya existente no tiene efecto)
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
