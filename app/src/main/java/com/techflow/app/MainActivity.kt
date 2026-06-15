package com.techflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // rememberNavController - crea y recuerda el controlador de navegación
                    // Este controlador maneja el backstack y la navegación entre pantallas
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
