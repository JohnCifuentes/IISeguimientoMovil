package com.example.iiseguimientomovil.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.iiseguimientomovil.data.local.entity.PokemonEntity

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemons ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPokemonsPage(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemons WHERE name LIKE '%' || :query || '%' ORDER BY id ASC LIMIT 100")
    suspend fun searchByName(query: String): List<PokemonEntity>

    @Query("SELECT * FROM pokemons WHERE name = :name LIMIT 1")
    suspend fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT * FROM pokemons WHERE id = :id LIMIT 1")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    @Query("SELECT COUNT(*) FROM pokemons")
    suspend fun getCount(): Int

    @Query("SELECT * FROM pokemons WHERE types LIKE '%' || :type || '%' ORDER BY id ASC")
    suspend fun getPokemonsByType(type: String): List<PokemonEntity>
}
