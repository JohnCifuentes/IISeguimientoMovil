package com.example.iiseguimientomovil.ui.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iiseguimientomovil.domain.model.Pokemon
import com.example.iiseguimientomovil.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PokemonListUiState(
    val pokemons: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val isOnline: Boolean = true,
    val searchQuery: String = "",
    val selectedType: String = "",
    val availableTypes: List<String> = emptyList(),
    val canLoadMore: Boolean = true
)

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 20

    init {
        observeConnectivity()
        loadTypes()
        loadPokemonList()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            repository.observeConnectivity().collect { isOnline ->
                val wasOffline = !_uiState.value.isOnline
                _uiState.update { it.copy(isOnline = isOnline) }
                // Si recupera conexión y la lista está vacía, recargar
                if (isOnline && wasOffline && _uiState.value.pokemons.isEmpty()) {
                    loadPokemonList()
                }
            }
        }
    }

    private fun loadTypes() {
        viewModelScope.launch {
            try {
                val types = repository.getTypes()
                _uiState.update { it.copy(availableTypes = types) }
            } catch (e: Exception) {
                // Los tipos no son críticos, ignorar error
            }
        }
    }

    fun loadPokemonList() {
        if (_uiState.value.isLoading) return
        currentOffset = 0
        _uiState.update {
            it.copy(
                isLoading = true,
                error = null,
                canLoadMore = true,
                searchQuery = "",
                selectedType = ""
            )
        }
        viewModelScope.launch {
            try {
                val pokemons = repository.getPokemonList(pageSize, currentOffset)
                currentOffset = pageSize
                _uiState.update {
                    it.copy(
                        pokemons = pokemons,
                        isLoading = false,
                        canLoadMore = pokemons.size == pageSize
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadMorePokemons() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.canLoadMore ||
            state.searchQuery.isNotBlank() || state.selectedType.isNotBlank()
        ) return
        _uiState.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            try {
                val pokemons = repository.getPokemonList(pageSize, currentOffset)
                currentOffset += pageSize
                _uiState.update {
                    it.copy(
                        pokemons = it.pokemons + pokemons,
                        isLoadingMore = false,
                        canLoadMore = pokemons.size == pageSize
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedType = "") }
        if (query.isBlank()) {
            loadPokemonList()
            return
        }
        viewModelScope.launch {
            try {
                val results = repository.searchPokemonByName(query)
                _uiState.update { it.copy(pokemons = results, canLoadMore = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun onTypeSelected(type: String) {
        if (type == _uiState.value.selectedType) {
            loadPokemonList()
            return
        }
        _uiState.update {
            it.copy(selectedType = type, searchQuery = "", isLoading = true, error = null)
        }
        viewModelScope.launch {
            try {
                val pokemons = repository.getPokemonByType(type)
                _uiState.update {
                    it.copy(pokemons = pokemons, isLoading = false, canLoadMore = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
