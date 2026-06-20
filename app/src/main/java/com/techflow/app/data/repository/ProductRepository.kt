package com.techflow.app.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.techflow.app.data.firebase.FirestoreService
import com.techflow.app.data.local.ProductDao
import com.techflow.app.data.local.ProductEntity
import com.techflow.app.data.remote.ApiService
import com.techflow.app.data.remote.ProductApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton - Hilt crea UNA sola instancia de este Repository para toda la app
// @Inject constructor - Hilt inyecta automáticamente el ProductDao, el ApiService y el FirestoreService que necesitamos
// El Repository es la ÚNICA fuente de datos que los ViewModels pueden usar
// Ni las vistas ni los ViewModels acceden directamente al DAO, la base de datos, la API o Firestore
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val apiService: ApiService,
    private val firestoreService: FirestoreService
) {

    // Obtiene todos los productos del usuario actual como Flow
    // Flow emite automáticamente cuando hay cambios en la base de datos
    // El ViewModel observa este Flow y la UI se actualiza sola (recomposición)
    fun getAllProducts(userId: String): Flow<List<ProductEntity>> {
        return productDao.getAllProducts(userId)
    }

    // Obtiene un producto por su ID local para mostrar en la pantalla de detalle
    // suspend = función suspendida, se ejecuta en una corrutina sin bloquear el hilo principal
    suspend fun getProductById(id: Int): ProductEntity? {
        return productDao.getProductById(id)
    }

    // Busca un producto local por su firestoreId, para saber si ya existe antes de insertarlo
    // de nuevo al sincronizar desde la nube (evita duplicados)
    suspend fun getProductByFirestoreId(firestoreId: String, userId: String): ProductEntity? {
        return productDao.getProductByFirestoreId(firestoreId, userId)
    }

    // Obtiene los productos con stock bajo (cantidad <= stockMinimo) para estadísticas
    fun getLowStockProducts(userId: String): Flow<List<ProductEntity>> {
        return productDao.getLowStockProducts(userId)
    }

    // Inserta un producto nuevo en Room y retorna el ID generado
    // Este ID se usa después para actualizar o eliminar el producto
    suspend fun insertProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    // Actualiza todos los campos de un producto existente en Room
    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    // Elimina un producto de Room por su objeto completo
    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    // Elimina todos los productos del usuario (se usa al cerrar sesión)
    suspend fun deleteAllProductsByUser(userId: String) {
        productDao.deleteAllProductsByUser(userId)
    }

    // Busca productos de electrónica en la API externa a través de Retrofit
    suspend fun searchProductsFromApi(): List<ProductApiResponse> {
        return apiService.getElectronicsProducts()
    }

    // --- Sincronización con la nube (Cloud Firestore) ---
    // Estas funciones delegan en FirestoreService para mantener el inventario respaldado
    // en usuarios/{userId}/productos, separado de la copia local que guarda Room

    // Sube un producto nuevo a Firestore y retorna el firestoreId generado
    // El orden es importante para evitar duplicados con el listener en tiempo real:
    // 1. Genera el firestoreId de forma sincrónica (sin escribir nada todavía)
    // 2. Actualiza la fila local en Room con ese firestoreId
    // 3. Recién después escribe en Firestore - cuando el listener reciba el snapshot,
    //    la fila local YA tiene el firestoreId correcto y la reconoce como existente
    suspend fun syncProductToFirestore(userId: String, product: ProductEntity): String {
        val firestoreId = firestoreService.generateFirestoreId(userId)
        productDao.updateProduct(product.copy(firestoreId = firestoreId))
        firestoreService.insertProduct(userId, firestoreId, product)
        return firestoreId
    }

    // Actualiza en Firestore el documento de un producto que ya existe en la nube
    suspend fun updateProductInFirestore(userId: String, firestoreId: String, product: ProductEntity) {
        firestoreService.updateProduct(userId, firestoreId, product)
    }

    // Elimina de Firestore el documento de un producto, manteniendo la nube sincronizada con Room
    suspend fun deleteProductFromFirestore(userId: String, firestoreId: String) {
        firestoreService.deleteProduct(userId, firestoreId)
    }

    // Obtiene todos los productos guardados en la nube para el usuario actual
    // Útil para restaurar el inventario en Room al iniciar sesión en un dispositivo nuevo
    suspend fun getProductsFromFirestore(userId: String): List<ProductEntity> {
        return firestoreService.getAllProducts(userId)
    }

    // Escucha en tiempo real los cambios en la nube para el usuario actual
    // A diferencia de getProductsFromFirestore (una sola consulta), este listener se mantiene
    // activo y notifica automáticamente cada vez que cambia algo en Firestore, sin necesidad
    // de volver a llamarlo manualmente. Retorna el ListenerRegistration para poder cancelarlo
    fun listenToFirestoreProducts(
        userId: String,
        onUpdate: (List<ProductEntity>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration = firestoreService.listenToProducts(userId, onUpdate, onError)
}
