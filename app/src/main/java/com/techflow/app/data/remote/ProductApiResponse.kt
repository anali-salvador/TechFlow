package com.techflow.app.data.remote

import com.google.gson.annotations.SerializedName

// ProductApiResponse - representa la respuesta JSON de la API de productos
// @SerializedName mapea los nombres del JSON a las propiedades de Kotlin
// Usamos la API de FakeStoreAPI que devuelve productos tecnológicos
data class ProductApiResponse(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("price")
    val price: Double = 0.0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("image")
    val image: String = ""
)
