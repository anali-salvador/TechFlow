package com.techflow.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.techflow.app.navigation.NavGraph
import com.techflow.app.ui.theme.TechFlowTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint - permite que Hilt inyecte dependencias en esta Activity
// Sin esta anotación, los ViewModels con @HiltViewModel no funcionarían
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // RF16 - registra el contrato para solicitar el permiso POST_NOTIFICATIONS en runtime
    // Debe registrarse como propiedad de la Activity, antes de que esta llegue a STARTED
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Concedido o denegado: si se deniega, NotificationHelper simplemente no notificará */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // RF16 - en Android 13+ (API 33) el permiso de notificaciones se pide en tiempo de ejecución
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // RF17 - si la Activity se abrió al tocar una notificación de stock bajo, lee el ID
        // del producto afectado para que el NavGraph navegue automáticamente a su detalle
        val notificationProductId = intent?.getIntExtra("productId", -1) ?: -1

        setContent {
            TechFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // rememberNavController - crea y recuerda el controlador de navegación
                    // Este controlador maneja el backstack y la navegación entre pantallas
                    val navController = rememberNavController()
                    NavGraph(navController = navController, initialProductId = notificationProductId)
                }
            }
        }
    }
}
