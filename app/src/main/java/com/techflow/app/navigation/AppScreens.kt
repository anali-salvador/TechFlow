package com.techflow.app.navigation

// sealed class AppScreens - define todas las rutas de navegación de la app
// Cada objeto representa una pantalla con su ruta única
// sealed class impide que se creen rutas fuera de esta clase (seguridad en compilación)
sealed class AppScreens(val route: String) {

    // Pantalla de lista de inventario (pantalla principal)
    object InventoryList : AppScreens("inventory_list")

    // Pantalla de detalle - recibe el ID del producto como argumento
    // {productId} es el placeholder que Navigation Compose reemplaza con el ID real
    object ProductDetail : AppScreens("product_detail/{productId}") {
        // Función que construye la ruta con el ID concreto
        // Ejemplo: createRoute(5) -> "product_detail/5"
        fun createRoute(productId: Int) = "product_detail/$productId"
    }

    // Pantalla de formulario para agregar un producto nuevo
    object ProductAdd : AppScreens("product_add")

    // Pantalla de formulario para editar un producto existente
    // Recibe el ID del producto para precargar sus datos
    object ProductEdit : AppScreens("product_edit/{productId}") {
        fun createRoute(productId: Int) = "product_edit/$productId"
    }

    // Pantalla de explorar - búsqueda por API externa
    object Explore : AppScreens("explore")

    // Pantalla de estadísticas del inventario
    object Statistics : AppScreens("statistics")

    // Pantallas de autenticación (Parte 2 - Firebase Auth)
    object Login : AppScreens("login")
    object Register : AppScreens("register")

    // Pantalla de historial de notificaciones (funcionalidad extra)
    object NotificationHistory : AppScreens("notification_history")

    // Pantalla de perfil de usuario de solo lectura
    object Profile : AppScreens("profile")
}
