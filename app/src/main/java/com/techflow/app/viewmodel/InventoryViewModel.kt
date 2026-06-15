package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techflow.app.domain.model.Product
import com.techflow.app.domain.model.toDomain
import com.techflow.app.domain.model.toEntity
import com.techflow.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt se encarga de crear este ViewModel e inyectar el Repository
// El ViewModel sobrevive a cambios de configuración (rotación de pantalla)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // _uiState es privado y mutable, solo el ViewModel puede modificarlo
    // uiState es público e inmutable, la vista solo puede leerlo (observarlo)
    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    // userId temporal hardcodeado - en la Parte 2 vendrá de Firebase Auth
    private var currentUserId: String = "local_user"

    // init se ejecuta cuando se crea el ViewModel por primera vez
    init {
        loadProducts()
    }

    // Carga todos los productos del usuario desde el Repository
    // viewModelScope - corrutina que se cancela automáticamente cuando el ViewModel se destruye
    // collect - escucha los cambios del Flow, cada vez que Room emite datos nuevos se actualiza el estado
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                productRepository.getAllProducts(currentUserId).collect { productEntities ->
                    // Convierte las entidades de Room a objetos Product del modelo
                    val products = productEntities.map { entity -> entity.toDomain() }
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar productos: ${e.message}"
                )
            }
        }
    }

    // Inserta un producto nuevo en Room a través del Repository
    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(
                    userId = currentUserId,
                    fechaRegistro = System.currentTimeMillis()
                ).toEntity()
                productRepository.insertProduct(entity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    // Actualiza un producto existente en Room
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(userId = currentUserId).toEntity()
                productRepository.updateProduct(entity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    // Elimina un producto de Room
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(userId = currentUserId).toEntity()
                productRepository.deleteProduct(entity)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al eliminar producto: ${e.message}"
                )
            }
        }
    }

    // Carga un producto por ID para la pantalla de detalle
    fun loadProductById(id: Int) {
        viewModelScope.launch {
            try {
                val entity = productRepository.getProductById(id)
                val product = entity?.toDomain()
                _uiState.value = _uiState.value.copy(selectedProduct = product)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al cargar producto: ${e.message}"
                )
            }
        }
    }

    // Limpia el mensaje de error después de mostrarlo
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
