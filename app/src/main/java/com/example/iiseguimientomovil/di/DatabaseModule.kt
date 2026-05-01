package com.example.iiseguimientomovil.di

import android.content.Context
import androidx.room.Room
import com.example.iiseguimientomovil.data.local.AppDatabase
import com.example.iiseguimientomovil.data.local.dao.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pokemon-database"
        ).build()
    }

    @Provides
    fun providePokemonDao(database: AppDatabase): PokemonDao {
        return database.pokemonDao()
    }
}
