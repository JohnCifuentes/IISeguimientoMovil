package com.example.iiseguimientomovil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.iiseguimientomovil.ui.pokemondetail.PokemonDetailScreen
import com.example.iiseguimientomovil.ui.pokemonlist.PokemonListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "pokemon_list"
    ) {
        composable("pokemon_list") {
            PokemonListScreen(navController = navController)
        }
        composable(
            route = "pokemon_detail/{pokemonName}",
            arguments = listOf(
                navArgument("pokemonName") { type = NavType.StringType }
            )
        ) {
            PokemonDetailScreen(navController = navController)
        }
    }
}
