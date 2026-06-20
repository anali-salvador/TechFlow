package com.techflow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techflow.app.data.local.ProductEntity
import com.techflow.app.data.local.STOCK_MINIMO_GLOBAL
import com.techflow.app.data.repository.AuthRepository
import com.techflow.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt inyecta ProductRepository y AuthRepository automáticamente
// Este ViewModel maneja la búsqueda de productos en la API externa
// y permite agregar productos encontrados al inventario local
// ProductRepository es la ÚNICA fuente de datos (Room, API y Firestore pasan por él)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    // RF14 - cada usuario solo ve y gestiona sus propios productos
    // currentUserId viene del UID real de Firebase Auth, no de un valor hardcodeado
    private val currentUserId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    // Busca productos de electrónica en la API externa
    // Se llama cuando el usuario presiona el botón buscar o al entrar a la pantalla
    fun searchProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                // Llama al endpoint de la API a través del Repository
                val products = productRepository.searchProductsFromApi()
                // Filtra por el texto de búsqueda si no está vacío
                val filtered = if (_uiState.value.searchQuery.isBlank()) {
                    products
                } else {
                    products.filter { product ->
                        product.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                        product.description.contains(_uiState.value.searchQuery, ignoreCase = true)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    apiProducts = filtered,
                    isLoading = false
                )
            } catch (e: Exception) {
                // Si no hay internet o la API falla, muestra error amigable
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión. Verifica tu internet e intenta de nuevo."
                )
            }
        }
    }

    // Actualiza el texto de búsqueda cuando el usuario escribe
    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    // RF15 - el CRUD se sincroniza automáticamente en Cloud Firestore
    // Agrega un producto de la API al inventario local en Room (fuente principal)
    // Convierte ProductApiResponse a ProductEntity con los campos del expediente técnico
    fun addProductToInventory(title: String, price: Double, description: String, category: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                val entity = ProductEntity(
                    firestoreId = "",
                    nombre = title,
                    categoria = category,
                    marca = "Importado",
                    precio = price,
                    cantidad = 1,
                    // Stock mínimo fijo del sistema, igual para todos los productos (también los importados)
                    stockMinimo = STOCK_MINIMO_GLOBAL,
                    descripcion = description,
                    imagenUrl = imageUrl,
                    userId = currentUserId,
                    fechaRegistro = System.currentTimeMillis()
                )
                val newId = productRepository.insertProduct(entity)
                val insertedEntity = entity.copy(id = newId.toInt())

                // Sincroniza con Firestore en su propio try/catch silencioso: si falla
                // (sin internet, error de red, etc.) el producto ya quedó guardado en Room
                // y no se pierde, un fallo en la nube no debe bloquear la operación local
                // syncProductToFirestore ya actualiza la fila local con el firestoreId generado
                // antes de escribir en la nube, así que no hace falta un updateProduct manual aquí
                try {
                    productRepository.syncProductToFirestore(currentUserId, insertedEntity)
                } catch (e: Exception) {
                    // Se ignora silenciosamente: el dato local en Room ya está seguro
                }

                _uiState.value = _uiState.value.copy(productAdded = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    // Resetea el flag de producto agregado después de mostrar el mensaje
    fun resetProductAdded() {
        _uiState.value = _uiState.value.copy(productAdded = false)
    }
}
