package com.example.iiseguimientomovil.domain.repository

import com.example.iiseguimientomovil.domain.model.Pokemon
import com.example.iiseguimientomovil.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon>
    suspend fun getPokemonDetail(name: String): PokemonDetail
    suspend fun getTypes(): List<String>
    suspend fun getPokemonByType(type: String): List<Pokemon>
    suspend fun searchPokemonByName(query: String): List<Pokemon>
    fun isConnected(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}
