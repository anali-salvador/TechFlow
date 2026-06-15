package com.techflow.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// @Dao - le dice a Room que esta interfaz contiene las operaciones de base de datos
@Dao
interface ProductDao {

    // @Query - consulta SQL para obtener todos los productos del usuario actual
    // Flow - emite automáticamente cuando los datos cambian, la UI se actualiza sola
    @Query("SELECT * FROM productos WHERE userId = :userId ORDER BY fechaRegistro DESC")
    fun getAllProducts(userId: String): Flow<List<ProductEntity>>

    // @Query - busca un producto por su ID local para mostrar en pantalla de detalle
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    // @Query - obtiene productos con cantidad <= stockMinimo para las estadísticas
    @Query("SELECT * FROM productos WHERE userId = :userId AND cantidad <= stockMinimo")
    fun getLowStockProducts(userId: String): Flow<List<ProductEntity>>

    // @Insert - inserta un producto nuevo, OnConflict.REPLACE reemplaza si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    // @Update - actualiza todos los campos de un producto existente en la base de datos
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // @Delete - elimina un producto de la base de datos local por su objeto completo
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // @Query - elimina todos los productos del usuario al cerrar sesión
    @Query("DELETE FROM productos WHERE userId = :userId")
    suspend fun deleteAllProductsByUser(userId: String)
}
