package com.example.iiseguimientomovil.data.remote

import com.example.iiseguimientomovil.data.remote.dto.PokemonDetailDto
import com.example.iiseguimientomovil.data.remote.dto.PokemonListResponseDto
import com.example.iiseguimientomovil.data.remote.dto.PokemonSpeciesDto
import com.example.iiseguimientomovil.data.remote.dto.TypeDetailResponseDto
import com.example.iiseguimientomovil.data.remote.dto.TypeListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Endpoint 1: Lista paginada de Pokémon
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    // Endpoint 2: Detalle completo de un Pokémon por nombre o ID
    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailDto

    // Endpoint 3: Lista de todos los tipos (para filtro dropdown)
    @GET("type")
    suspend fun getTypes(
        @Query("limit") limit: Int = 100
    ): TypeListResponseDto

    // Endpoint 4: Pokémon filtrados por tipo
    @GET("type/{name}")
    suspend fun getPokemonByType(
        @Path("name") name: String
    ): TypeDetailResponseDto

    // Endpoint 5: Información de especie (flavor text, habitat)
    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(
        @Path("name") name: String
    ): PokemonSpeciesDto
}
