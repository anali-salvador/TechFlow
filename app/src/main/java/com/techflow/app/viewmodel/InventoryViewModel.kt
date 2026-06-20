package com.techflow.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.techflow.app.domain.model.Product
import com.techflow.app.domain.model.toDomain
import com.techflow.app.domain.model.toEntity
import com.techflow.app.data.repository.AuthRepository
import com.techflow.app.data.repository.ProductRepository
import com.techflow.app.notifications.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel - Hilt se encarga de crear este ViewModel e inyectar el Repository
// El ViewModel sobrevive a cambios de configuración (rotación de pantalla)
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository,
    // NotificationHelper ahora es una clase inyectable (necesita el NotificationDao
    // para guardar el historial de notificaciones), ya no se llama como object estático
    private val notificationHelper: NotificationHelper,
    // @ApplicationContext - Hilt inyecta el contexto de la aplicación, necesario para mostrar
    // notificaciones (RF16) sin acoplar el ViewModel a una Activity específica
    @ApplicationContext private val context: Context
) : ViewModel() {

    // _uiState es privado y mutable, solo el ViewModel puede modificarlo
    // uiState es público e inmutable, la vista solo puede leerlo (observarlo)
    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    // RF14 - cada usuario solo ve y gestiona sus propios productos
    // currentUserId ahora viene del UID real de Firebase Auth, no de un valor hardcodeado
    // String vacío como fallback de seguridad si por algún motivo no hay sesión activa
    // (no debería ocurrir porque la navegación ya protege el acceso a esta pantalla)
    private var currentUserId: String = ""

    // Referencia al listener activo de Firestore, necesaria para cancelarlo en onCleared()
    // y evitar fugas de memoria (el listener seguiría escuchando incluso sin ViewModel)
    private var firestoreListener: ListenerRegistration? = null

    // init se ejecuta cuando se crea el ViewModel por primera vez
    // (cada vez que el usuario entra a la pantalla de inventario)
    init {
        currentUserId = authRepository.getCurrentUser()?.uid ?: ""
        listenToFirestoreChanges()
        loadProducts()
    }

    // RF15 - sincronización en tiempo real desde Firestore hacia Room
    // A diferencia de una consulta única, este listener se mantiene activo: cada cambio en
    // Firestore (agregado desde cualquier dispositivo con la misma cuenta) llega aquí
    // automáticamente y se refleja en Room sin que el usuario tenga que salir y volver a entrar
    private fun listenToFirestoreChanges() {
        firestoreListener = productRepository.listenToFirestoreProducts(
            userId = currentUserId,
            onUpdate = { firestoreProducts ->
                viewModelScope.launch {
                    firestoreProducts.forEach { cloudProduct ->
                        // Compara por firestoreId: si ya existe en Room, actualiza sus campos
                        // para reflejar cambios hechos desde otro dispositivo; si no existe, lo inserta
                        val existing = productRepository.getProductByFirestoreId(
                            cloudProduct.firestoreId,
                            currentUserId
                        )
                        if (existing == null) {
                            productRepository.insertProduct(cloudProduct)
                        } else {
                            productRepository.updateProduct(cloudProduct.copy(id = existing.id))
                        }
                    }

                    // RF15 - sincronización bidireccional completa: lo que se elimina en la nube
                    // (desde cualquier dispositivo o directamente desde la consola de Firebase)
                    // también debe eliminarse localmente, no solo lo agregado/actualizado
                    // first() toma el valor actual del Flow de Room una sola vez (no se suscribe)
                    val localProducts = productRepository.getAllProducts(currentUserId).first()
                    val firestoreIds = firestoreProducts.map { it.firestoreId }.toSet()
                    localProducts.forEach { localProduct ->
                        // Solo elimina productos que ya estaban sincronizados con la nube
                        // (firestoreId no vacío) y que ya no aparecen en el snapshot actual;
                        // los que todavía no se sincronizaron (firestoreId vacío) se conservan
                        if (localProduct.firestoreId.isNotEmpty() && localProduct.firestoreId !in firestoreIds) {
                            productRepository.deleteProduct(localProduct)
                        }
                    }
                }
            },
            onError = {
                // Sin internet, error de Firestore, etc. No rompe la carga normal:
                // loadProducts() sigue mostrando lo que ya hay en Room
            }
        )
    }

    // Cancela el listener de Firestore cuando el ViewModel se destruye
    // (al salir definitivamente de la pantalla de inventario), evitando fugas de memoria
    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove()
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

    // RF15 - el CRUD se sincroniza automáticamente en Cloud Firestore
    // Inserta un producto nuevo en Room a través del Repository
    // Room es la fuente principal de datos: funciona sin conexión a internet
    // Firestore es solo el respaldo en la nube, por eso va en su propio try/catch
    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(
                    userId = currentUserId,
                    fechaRegistro = System.currentTimeMillis()
                ).toEntity()
                // insertProduct retorna el ID autogenerado por Room, necesario para la notificación
                // y para que syncProductToFirestore pueda actualizar la fila local correcta
                val newId = productRepository.insertProduct(entity)
                val insertedEntity = entity.copy(id = newId.toInt())

                // Sincroniza con Firestore en un try/catch separado: si falla (sin internet,
                // error de red, etc.) el producto ya quedó guardado en Room y no se pierde,
                // un fallo en la nube no debe bloquear ni reportar error en la operación local
                // syncProductToFirestore ya actualiza la fila local con el firestoreId generado
                // antes de escribir en la nube, así que no hace falta un updateProduct manual aquí
                try {
                    productRepository.syncProductToFirestore(currentUserId, insertedEntity)
                } catch (e: Exception) {
                    // Se ignora silenciosamente: el dato local en Room ya está seguro
                }

                // RF16 - notifica si el producto ya nace con stock bajo (cantidad <= stockMinimo)
                if (insertedEntity.cantidad <= insertedEntity.stockMinimo) {
                    notificationHelper.showLowStockNotification(
                        context,
                        newId.toInt(),
                        insertedEntity.nombre,
                        insertedEntity.cantidad
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al agregar producto: ${e.message}"
                )
            }
        }
    }

    // RF15 - el CRUD se sincroniza automáticamente en Cloud Firestore
    // Actualiza un producto existente en Room (fuente principal) y luego en Firestore (respaldo)
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(userId = currentUserId).toEntity()
                productRepository.updateProduct(entity)

                // Solo sincroniza con Firestore si el producto ya tiene un firestoreId asignado
                // Un fallo aquí no debe bloquear la actualización local, ya aplicada en Room
                if (entity.firestoreId.isNotEmpty()) {
                    try {
                        productRepository.updateProductInFirestore(currentUserId, entity.firestoreId, entity)
                    } catch (e: Exception) {
                        // Se ignora silenciosamente: el dato local en Room ya está actualizado
                    }
                }

                // RF16 - notifica si tras la actualización el producto queda con stock bajo
                if (entity.cantidad <= entity.stockMinimo) {
                    notificationHelper.showLowStockNotification(
                        context,
                        entity.id,
                        entity.nombre,
                        entity.cantidad
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al actualizar producto: ${e.message}"
                )
            }
        }
    }

    // RF15 - el CRUD se sincroniza automáticamente en Cloud Firestore
    // Elimina un producto de Room (fuente principal) y luego de Firestore (respaldo)
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                val entity = product.copy(userId = currentUserId).toEntity()
                productRepository.deleteProduct(entity)

                // Solo sincroniza con Firestore si el producto ya tiene un firestoreId asignado
                // Un fallo aquí no debe bloquear la eliminación local, ya aplicada en Room
                if (entity.firestoreId.isNotEmpty()) {
                    try {
                        productRepository.deleteProductFromFirestore(currentUserId, entity.firestoreId)
                    } catch (e: Exception) {
                        // Se ignora silenciosamente: el dato local en Room ya fue eliminado
                    }
                }
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
