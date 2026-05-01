package com.example.iiseguimientomovil.data.repository

import com.example.iiseguimientomovil.data.local.dao.PokemonDao
import com.example.iiseguimientomovil.data.local.entity.PokemonEntity
import com.example.iiseguimientomovil.data.remote.ApiService
import com.example.iiseguimientomovil.domain.model.Pokemon
import com.example.iiseguimientomovil.domain.model.PokemonDetail
import com.example.iiseguimientomovil.domain.model.PokemonStat
import com.example.iiseguimientomovil.domain.repository.PokemonRepository
import com.example.iiseguimientomovil.util.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao,
    private val connectivityObserver: ConnectivityObserver
) : PokemonRepository {

    override fun isConnected(): Boolean = connectivityObserver.isConnected()

    override fun observeConnectivity(): Flow<Boolean> = connectivityObserver.observe()

    override suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return if (isConnected()) {
            val response = apiService.getPokemonList(limit, offset)
            val entities = response.results.mapNotNull { item ->
                val id = item.url.trimEnd('/').split('/').lastOrNull()?.toIntOrNull() ?: return@mapNotNull null
                PokemonEntity(
                    id = id,
                    name = item.name,
                    spriteUrl = spriteUrlFromId(id)
                )
            }
            // Solo insertar si no existen (para no sobreescribir datos de detalle ya guardados)
            pokemonDao.insertAllIgnore(entities)
            pokemonDao.getPokemonsPage(limit, offset).map { it.toDomain() }
        } else {
            pokemonDao.getPokemonsPage(limit, offset).map { it.toDomain() }
        }
    }

    override suspend fun getPokemonDetail(name: String): PokemonDetail {
        return if (isConnected()) {
            val detailDto = apiService.getPokemonDetail(name)
            val flavorText = try {
                val speciesDto = apiService.getPokemonSpecies(name)
                speciesDto.flavorTextEntries
                    .firstOrNull { it.language.name == "en" }
                    ?.flavorText
                    ?.replace("\n", " ")
                    ?.replace("\u000c", " ")
                    ?: ""
            } catch (e: Exception) {
                ""
            }
            val entity = PokemonEntity(
                id = detailDto.id,
                name = detailDto.name,
                spriteUrl = detailDto.sprites.frontDefault ?: spriteUrlFromId(detailDto.id),
                types = detailDto.types
                    .sortedBy { it.slot }
                    .joinToString(",") { it.type.name },
                height = detailDto.height,
                weight = detailDto.weight,
                baseExperience = detailDto.baseExperience ?: 0,
                stats = detailDto.stats.joinToString(";") { "${it.stat.name}:${it.baseStat}" },
                abilities = detailDto.abilities
                    .sortedBy { it.slot }
                    .joinToString(",") { it.ability.name },
                flavorText = flavorText,
                detailLoaded = true
            )
            pokemonDao.insert(entity)
            entity.toDetailDomain()
        } else {
            pokemonDao.getPokemonByName(name)?.toDetailDomain()
                ?: throw Exception("Pokémon no encontrado sin conexión")
        }
    }

    override suspend fun getTypes(): List<String> {
        return if (isConnected()) {
            apiService.getTypes(100)
                .results
                .map { it.name }
                .filter { it != "unknown" && it != "shadow" }
        } else {
            emptyList()
        }
    }

    override suspend fun getPokemonByType(type: String): List<Pokemon> {
        return if (isConnected()) {
            val response = apiService.getPokemonByType(type)
            val entities = response.pokemon.mapNotNull { slot ->
                val id = slot.pokemon.url.trimEnd('/').split('/').lastOrNull()?.toIntOrNull()
                    ?: return@mapNotNull null
                if (id > 10000) return@mapNotNull null // omitir formas alternativas
                val existing = pokemonDao.getPokemonById(id)
                if (existing != null) {
                    // Actualizar tipos si no estaban guardados
                    if (existing.types.isBlank()) {
                        pokemonDao.insert(existing.copy(types = type))
                    }
                    existing.toDomain().copy(
                        types = if (existing.types.isBlank()) listOf(type)
                        else existing.types.split(",")
                    )
                } else {
                    val entity = PokemonEntity(
                        id = id,
                        name = slot.pokemon.name,
                        spriteUrl = spriteUrlFromId(id),
                        types = type
                    )
                    pokemonDao.insert(entity)
                    entity.toDomain()
                }
            }.sortedBy { it.id }
            entities
        } else {
            pokemonDao.getPokemonsByType(type).map { it.toDomain() }
        }
    }

    override suspend fun searchPokemonByName(query: String): List<Pokemon> {
        return if (query.isBlank()) emptyList()
        else pokemonDao.searchByName(query).map { it.toDomain() }
    }

    private fun spriteUrlFromId(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}

// ── Mappers ────────────────────────────────────────────────────────────────

fun PokemonEntity.toDomain() = Pokemon(
    id = id,
    name = name,
    spriteUrl = spriteUrl,
    types = if (types.isBlank()) emptyList() else types.split(",")
)

fun PokemonEntity.toDetailDomain() = PokemonDetail(
    id = id,
    name = name,
    spriteUrl = spriteUrl,
    types = if (types.isBlank()) emptyList() else types.split(","),
    height = height,
    weight = weight,
    baseExperience = baseExperience,
    stats = if (stats.isBlank()) emptyList() else stats.split(";").mapNotNull { s ->
        val parts = s.split(":")
        if (parts.size == 2) PokemonStat(parts[0], parts[1].toIntOrNull() ?: 0) else null
    },
    abilities = if (abilities.isBlank()) emptyList() else abilities.split(","),
    flavorText = flavorText
)
