package com.techflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techflow.app.ui.auth.LoginScreen
import com.techflow.app.ui.auth.RegisterScreen
import com.techflow.app.ui.splash.SplashScreen
import com.techflow.app.ui.explore.ExploreScreen
import com.techflow.app.ui.inventory.InventoryListScreen
import com.techflow.app.ui.inventory.ProductDetailScreen
import com.techflow.app.ui.inventory.ProductFormScreen
import com.techflow.app.ui.notifications.NotificationHistoryScreen
import com.techflow.app.ui.profile.ProfileScreen
import com.techflow.app.ui.statistics.StatisticsScreen
import com.techflow.app.viewmodel.AuthViewModel

// NavGraph - define todas las rutas y la navegación entre pantallas
// NavHost muestra la pantalla actual según la ruta activa
// startDestination = Login, la primera pantalla al abrir la app (Parte 2 - Firebase Auth)
// initialProductId - RF17: si la app se abrió desde una notificación de stock bajo,
// distinto de -1 indica que se debe navegar automáticamente al detalle de ese producto
@Composable
fun NavGraph(navController: NavHostController, initialProductId: Int = -1) {

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {
        // Splash Screen — punto de entrada; decide a dónde navegar según sesión activa
        composable(route = AppScreens.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                onSplashFinished = {
                    val isLoggedIn = authViewModel.uiState.value.isAuthenticated
                    if (isLoggedIn) {
                        navController.navigate(AppScreens.InventoryList.route) {
                            popUpTo(AppScreens.Splash.route) { inclusive = true }
                        }
                        // RF17 - si se abrió desde notificación de stock bajo, va directo al detalle
                        if (initialProductId != -1) {
                            navController.navigate(AppScreens.ProductDetail.createRoute(initialProductId))
                        }
                    } else {
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(AppScreens.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Pantalla de Login - Parte 2 (Firebase Authentication)
        composable(route = AppScreens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // popUpTo(Login, inclusive = true) elimina Login del backstack
                    // así el botón atrás no puede volver a la pantalla de login
                    navController.navigate(AppScreens.InventoryList.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppScreens.Register.route)
                }
            )
        }

        // Pantalla de Registro - Parte 2 (Firebase Authentication)
        composable(route = AppScreens.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Mismo comportamiento que el login exitoso: limpia el backstack hasta Login
                    navController.navigate(AppScreens.InventoryList.route) {
                        popUpTo(AppScreens.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        // Pantalla principal - Lista de Inventario
        composable(route = AppScreens.InventoryList.route) {
            // AuthViewModel propio de esta entrada del backstack, solo para cerrar sesión
            val authViewModel: AuthViewModel = hiltViewModel()
            InventoryListScreen(
                onProductClick = { productId ->
                    navController.navigate(AppScreens.ProductDetail.createRoute(productId))
                },
                onAddClick = {
                    navController.navigate(AppScreens.ProductAdd.route)
                },
                onExploreClick = {
                    navController.navigate(AppScreens.Explore.route)
                },
                onStatisticsClick = {
                    navController.navigate(AppScreens.Statistics.route)
                },
                onLogoutClick = {
                    // Cierra la sesión en Firebase Auth y vuelve a Login limpiando el backstack
                    // así el botón atrás no puede volver al inventario sin sesión activa
                    authViewModel.logout()
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.InventoryList.route) { inclusive = true }
                    }
                },
                onNotificationsClick = {
                    navController.navigate(AppScreens.NotificationHistory.route)
                },
                onProfileClick = {
                    navController.navigate(AppScreens.Profile.route)
                }
            )
        }

        // Pantalla de Perfil de usuario (solo lectura)
        composable(route = AppScreens.Profile.route) {
            // AuthViewModel propio de esta entrada del backstack, solo para cerrar sesión
            val authViewModel: AuthViewModel = hiltViewModel()
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    // Mismo patrón de logout que ya usa InventoryListScreen
                    authViewModel.logout()
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.InventoryList.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Historial de Notificaciones (funcionalidad extra)
        composable(route = AppScreens.NotificationHistory.route) {
            NotificationHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(AppScreens.ProductDetail.createRoute(productId))
                }
            )
        }

        // Pantalla de Detalle del Producto
        composable(
            route = AppScreens.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(AppScreens.ProductEdit.createRoute(id))
                }
            )
        }

        // Pantalla Agregar Producto
        composable(route = AppScreens.ProductAdd.route) {
            ProductFormScreen(
                productId = null,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla Editar Producto
        composable(
            route = AppScreens.ProductEdit.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            ProductFormScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla Explorar - Búsqueda por API
        composable(route = AppScreens.Explore.route) {
            ExploreScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Pantalla Estadísticas
        composable(route = AppScreens.Statistics.route) {
            StatisticsScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(AppScreens.ProductDetail.createRoute(productId))
                }
            )
        }
    }

}
