package com.techflow.app.di

import android.content.Context
import androidx.room.Room
import com.techflow.app.data.local.ProductDao
import com.techflow.app.data.local.TechFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module - le dice a Hilt que esta clase contiene instrucciones para crear objetos
// @InstallIn(SingletonComponent) - estos objetos viven durante toda la vida de la app
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // @Provides - le dice a Hilt CÓMO crear la base de datos Room
    // @Singleton - solo se crea UNA instancia de la base de datos para toda la app
    // @ApplicationContext - Hilt inyecta el contexto de la aplicación automáticamente
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TechFlowDatabase {
        return Room.databaseBuilder(
            context,
            TechFlowDatabase::class.java,
            "techflow_database" // nombre del archivo de la base de datos en el dispositivo
        ).build()
    }

    // @Provides - le dice a Hilt CÓMO obtener el DAO a partir de la base de datos
    // Hilt ya sabe crear TechFlowDatabase (por el método de arriba), así que lo inyecta aquí
    @Provides
    @Singleton
    fun provideProductDao(database: TechFlowDatabase): ProductDao {
        return database.productDao()
    }
}
