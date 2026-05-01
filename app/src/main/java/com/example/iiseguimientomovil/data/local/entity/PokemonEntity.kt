package com.example.iiseguimientomovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val spriteUrl: String,
    val types: String = "",             // Tipos separados por coma: "fire,flying"
    val height: Int = 0,
    val weight: Int = 0,
    val baseExperience: Int = 0,
    val stats: String = "",             // "hp:45;attack:49;defense:49"
    val abilities: String = "",         // Habilidades separadas por coma
    val flavorText: String = "",
    val detailLoaded: Boolean = false
)
