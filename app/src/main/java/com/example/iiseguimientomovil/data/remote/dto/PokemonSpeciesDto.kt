package com.example.iiseguimientomovil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesDto(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>,
    val habitat: NamedResourceDto?,
    @SerializedName("is_legendary") val isLegendary: Boolean,
    @SerializedName("is_mythical") val isMythical: Boolean
)

data class FlavorTextEntryDto(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedResourceDto,
    val version: NamedResourceDto
)
