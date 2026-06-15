package com.techflow.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp - activa Hilt en toda la aplicación
// Esta anotación genera el código necesario para que Hilt pueda inyectar dependencias
// Sin esta clase, Hilt no funciona y la app crashea al iniciar
// Se debe registrar en el AndroidManifest.xml con android:name=".TechFlowApp"
@HiltAndroidApp
class TechFlowApp : Application()
