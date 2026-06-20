package com.techflow.app.domain.model

import com.techflow.app.data.local.ProductEntity

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        firestoreId = firestoreId,
        nombre = nombre,
        categoria = categoria,
        marca = marca,
        precio = precio,
        cantidad = cantidad,
        stockMinimo = stockMinimo,
        descripcion = descripcion,
        imagenUrl = imagenUrl,
        userId = userId,
        fechaRegistro = fechaRegistro
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        firestoreId = firestoreId,
        nombre = nombre,
        categoria = categoria,
        marca = marca,
        precio = precio,
        cantidad = cantidad,
        stockMinimo = stockMinimo,
        descripcion = descripcion,
        imagenUrl = imagenUrl,
        userId = userId,
        fechaRegistro = fechaRegistro
    )
}
