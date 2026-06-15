package com.techflow.app.viewmodel

import com.techflow.app.domain.model.Product

// StatisticsUiState - estado de la pantalla de Estadísticas
// El expediente técnico pide: total de productos, productos con stock bajo, valor total del inventario
data class StatisticsUiState(
    // Total de productos registrados en el inventario
    val totalProducts: Int = 0,
    // Cantidad de productos con stock bajo (cantidad <= stockMinimo)
    val lowStockCount: Int = 0,
    // Valor total estimado del inventario (suma de precio x cantidad de cada producto)
    val totalInventoryValue: Double = 0.0,
    // Lista de productos con stock bajo para mostrar con acceso rápido a su detalle
    val lowStockProducts: List<Product> = emptyList(),
    // true mientras se calculan las estadísticas
    val isLoading: Boolean = true
)
