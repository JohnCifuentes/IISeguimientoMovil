package com.example.iiseguimientomovil.data.remote.dto

data class TypeListResponseDto(
    val results: List<NamedResourceDto>
)

data class TypeDetailResponseDto(
    val pokemon: List<TypePokemonSlotDto>
)

data class TypePokemonSlotDto(
    val pokemon: NamedResourceDto,
    val slot: Int
)
