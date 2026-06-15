package com.techflow.app.di

import com.techflow.app.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// @Module - le dice a Hilt que esta clase provee objetos relacionados con la red
// @InstallIn(SingletonComponent) - viven durante toda la vida de la app
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // URL base de la API pública FakeStoreAPI
    // Todos los endpoints se construyen a partir de esta URL
    // Ejemplo: BASE_URL + "products" = "https://fakestoreapi.com/products"
    private const val BASE_URL = "https://fakestoreapi.com/"

    // @Provides - le dice a Hilt CÓMO crear la instancia de Retrofit
    // Retrofit se encarga de hacer las llamadas HTTP y convertir JSON a objetos Kotlin
    // GsonConverterFactory convierte automáticamente el JSON de la respuesta
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // @Provides - le dice a Hilt CÓMO crear el ApiService a partir de Retrofit
    // Retrofit genera la implementación de la interfaz ApiService automáticamente
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
