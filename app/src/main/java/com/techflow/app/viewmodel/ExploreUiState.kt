package com.techflow.app.viewmodel

import com.techflow.app.data.remote.ProductApiResponse

// ExploreUiState - estado de la pantalla Explorar/Búsqueda por API
// isLoading muestra el indicador circular mientras Retrofit obtiene la respuesta
// errorMessage muestra mensaje amigable si no hay conexión o la API falla
data class ExploreUiState(
    // Lista de productos obtenidos de la API externa
    val apiProducts: List<ProductApiResponse> = emptyList(),
    // true mientras se hace la llamada HTTP a la API
    val isLoading: Boolean = false,
    // mensaje de error si falla la conexión o la API
    val errorMessage: String? = null,
    // texto que el usuario escribió en el campo de búsqueda
    val searchQuery: String = "",
    // true cuando el producto se agregó exitosamente al inventario local
    val productAdded: Boolean = false
)
