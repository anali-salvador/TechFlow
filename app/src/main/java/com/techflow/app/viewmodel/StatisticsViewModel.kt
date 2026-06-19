package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techflow.app.domain.model.toDomain
import com.techflow.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt inyecta el ProductRepository para obtener los datos
// Calcula las estadísticas del inventario: total, stock bajo y valor total
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // userId temporal - en la Parte 2 vendrá de Firebase Auth
    private var currentUserId: String = "local_user"

    init {
        loadStatistics()
    }

    // Carga las estadísticas combinando dos Flows de Room
    // combine une el Flow de todos los productos y el Flow de productos con stock bajo
    // Cada vez que Room emite cambios en cualquiera de los dos, se recalculan las estadísticas
    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // combine - combina dos Flows y emite cuando cualquiera de los dos cambia
                combine(
                    productRepository.getAllProducts(currentUserId),
                    productRepository.getLowStockProducts(currentUserId)
                ) { allProducts, lowStockEntities ->

                    // Calcula el valor total: suma de (precio * cantidad) de cada producto
                    val totalValue = allProducts.sumOf { it.precio * it.cantidad }

                    // Calcula la distribución por categorías para la gráfica circular
                    val categoryDist = allProducts.groupBy { it.categoria }
                        .mapValues { it.value.size }

                    // Convierte las entidades de stock bajo a objetos Product
                    val lowStockProducts = lowStockEntities.map { entity -> entity.toDomain() }

                    // Retorna el nuevo estado con todas las estadísticas calculadas
                    StatisticsUiState(
                        totalProducts = allProducts.size,
                        lowStockCount = lowStockEntities.size,
                        totalInventoryValue = totalValue,
                        categoryDistribution = categoryDist,
                        lowStockProducts = lowStockProducts,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
