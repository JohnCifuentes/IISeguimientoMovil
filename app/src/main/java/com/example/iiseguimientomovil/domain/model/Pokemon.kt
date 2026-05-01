package com.example.iiseguimientomovil.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val spriteUrl: String,
    val types: List<String>
)
