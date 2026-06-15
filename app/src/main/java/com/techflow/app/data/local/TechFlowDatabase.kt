package com.techflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// @Database - define la base de datos Room con la lista de entidades (tablas)
// version = 1 - es la primera versión del esquema, si cambias la entidad debes subir el número
// exportSchema = false - no exporta el esquema a un archivo JSON (no es necesario para este proyecto)
@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class TechFlowDatabase : RoomDatabase() {

    // Función abstracta que Room implementa automáticamente para darnos acceso al DAO
    // A través de este DAO hacemos todas las operaciones CRUD en la tabla "productos"
    abstract fun productDao(): ProductDao
}
