package com.techflow.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity - le dice a Room que esta clase representa una tabla en la base de datos local
@Entity(tableName = "productos")
data class ProductEntity(
    // @PrimaryKey autoGenerate - Room genera el ID automáticamente, no lo asignamos nosotros
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // firestoreId - guarda el ID del documento en Firestore para sincronizar después
    val firestoreId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val marca: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    // stockMinimo - cuando cantidad <= stockMinimo se dispara la notificación local
    val stockMinimo: Int = 0,
    val descripcion: String? = null,
    // userId - UID de Firebase Auth, asegura que cada usuario solo vea sus productos
    val userId: String = "",
    // fechaRegistro - timestamp en milisegundos para ordenar productos por fecha
    val fechaRegistro: Long = System.currentTimeMillis()
)
