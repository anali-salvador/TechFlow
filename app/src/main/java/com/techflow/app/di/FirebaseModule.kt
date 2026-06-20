package com.techflow.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module - le dice a Hilt que esta clase provee objetos relacionados con Firebase
// @InstallIn(SingletonComponent) - estos objetos viven durante toda la vida de la app
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // @Provides - le dice a Hilt CÓMO obtener la instancia de FirebaseAuth
    // @Singleton - solo se crea UNA instancia de FirebaseAuth para toda la app
    // FirebaseAuth.getInstance() - obtiene la instancia ya configurada por google-services.json
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // @Provides - le dice a Hilt CÓMO obtener la instancia de Cloud Firestore
    // @Singleton - solo se crea UNA instancia de Firestore para toda la app
    // FirebaseFirestore.getInstance() - obtiene la base de datos en la nube configurada por el proyecto Firebase
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
