package com.techflow.app.viewmodel

import com.techflow.app.domain.model.Product

// data class UiState - contiene TODO el estado de la pantalla de inventario
// El ViewModel expone este estado con StateFlow y la vista lo observa con collectAsState()
// Cuando cualquier campo cambia, Compose hace recomposición automática de la UI
data class InventoryUiState(
    // Lista de productos del usuario que se muestra en LazyColumn
    val products: List<Product> = emptyList(),
    // true mientras se cargan los productos de Room (muestra indicador de carga)
    val isLoading: Boolean = true,
    // mensaje de error si algo falla al cargar los productos
    val errorMessage: String? = null,
    // producto seleccionado para la pantalla de detalle
    val selectedProduct: Product? = null
)
