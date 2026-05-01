package com.example.iiseguimientomovil.data.remote.dto

data class PokemonListResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItemDto>
)

data class PokemonListItemDto(
    val name: String,
    val url: String
)
