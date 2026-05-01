package com.example.iiseguimientomovil.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val spriteUrl: String,
    val types: List<String>,
    val height: Int,
    val weight: Int,
    val baseExperience: Int,
    val stats: List<PokemonStat>,
    val abilities: List<String>,
    val flavorText: String
)

data class PokemonStat(
    val name: String,
    val value: Int
)
