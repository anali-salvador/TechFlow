package com.techflow.app.domain.model

data class Product(
    val id: Int = 0,
    val firestoreId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val marca: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val stockMinimo: Int = 0,
    val descripcion: String? = null,
    val imagenUrl: String? = null,
    val userId: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)
