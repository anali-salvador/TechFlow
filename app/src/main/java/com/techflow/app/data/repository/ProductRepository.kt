package com.techflow.app.data.repository

import com.techflow.app.data.local.ProductDao
import com.techflow.app.data.local.ProductEntity
import com.techflow.app.data.remote.ApiService
import com.techflow.app.data.remote.ProductApiResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton - Hilt crea UNA sola instancia de este Repository para toda la app
// @Inject constructor - Hilt inyecta automáticamente el ProductDao y el ApiService que necesitamos
// El Repository es la ÚNICA fuente de datos que los ViewModels pueden usar
// Ni las vistas ni los ViewModels acceden directamente al DAO, la base de datos o la API
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val apiService: ApiService
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
}
