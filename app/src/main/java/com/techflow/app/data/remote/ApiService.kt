package com.techflow.app.data.remote

import retrofit2.http.GET

// ApiService - interfaz que define los endpoints de la API REST
// Retrofit genera la implementación automáticamente en tiempo de compilación
// Cada función suspend se ejecuta en una corrutina sin bloquear el hilo principal
// Usamos FakeStoreAPI (https://fakestoreapi.com) - API pública gratuita sin API key
interface ApiService {

    // GET - obtiene productos de electrónica de la categoría "electronics"
    // Retrofit convierte el JSON de respuesta a List<ProductApiResponse> automáticamente con Gson
    @GET("products/category/electronics")
    suspend fun getElectronicsProducts(): List<ProductApiResponse>
}
