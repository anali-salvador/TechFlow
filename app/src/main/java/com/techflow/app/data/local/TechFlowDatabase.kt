package com.techflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// @Database - define la base de datos Room con la lista de entidades (tablas)
// version = 3 - se sube en 1 al agregar NotificationEntity (historial de notificaciones, funcionalidad extra)
// fallbackToDestructiveMigration (en DatabaseModule) recrea las tablas si no hay migración, no es necesario definir una aquí
// exportSchema = false - no exporta el esquema a un archivo JSON (no es necesario para este proyecto)
@Database(entities = [ProductEntity::class, NotificationEntity::class], version = 3, exportSchema = false)
abstract class TechFlowDatabase : RoomDatabase() {

    // Función abstracta que Room implementa automáticamente para darnos acceso al DAO
    // A través de este DAO hacemos todas las operaciones CRUD en la tabla "productos"
    abstract fun productDao(): ProductDao

    // Acceso al DAO de la tabla "notificaciones" (historial de notificaciones)
    abstract fun notificationDao(): NotificationDao
}
