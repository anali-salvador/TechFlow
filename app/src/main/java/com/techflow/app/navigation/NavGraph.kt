package com.techflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techflow.app.ui.explore.ExploreScreen
import com.techflow.app.ui.inventory.InventoryListScreen
import com.techflow.app.ui.inventory.ProductDetailScreen
import com.techflow.app.ui.inventory.ProductFormScreen
import com.techflow.app.ui.statistics.StatisticsScreen

// NavGraph - define todas las rutas y la navegación entre pantallas
// NavHost muestra la pantalla actual según la ruta activa
// startDestination = la primera pantalla al abrir la app (en Parte 2 será Login)
@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AppScreens.InventoryList.route
    ) {
        // Pantalla principal - Lista de Inventario
        composable(route = AppScreens.InventoryList.route) {
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
