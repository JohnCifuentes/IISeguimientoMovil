package com.example.iiseguimientomovil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerializedName("base_experience") val baseExperience: Int?,
    val types: List<TypeSlotDto>,
    val stats: List<StatDto>,
    val abilities: List<AbilitySlotDto>,
    val sprites: SpritesDto
)

data class TypeSlotDto(
    val slot: Int,
    val type: NamedResourceDto
)

data class StatDto(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResourceDto
)

data class AbilitySlotDto(
    val ability: NamedResourceDto,
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int
)

data class SpritesDto(
    @SerializedName("front_default") val frontDefault: String?
)
