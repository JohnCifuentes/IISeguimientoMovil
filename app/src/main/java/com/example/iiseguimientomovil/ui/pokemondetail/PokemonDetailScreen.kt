package com.example.iiseguimientomovil.ui.pokemondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.iiseguimientomovil.domain.model.PokemonDetail
import com.example.iiseguimientomovil.domain.model.PokemonStat
import com.example.iiseguimientomovil.ui.pokemonlist.TypeChip
import com.example.iiseguimientomovil.ui.pokemonlist.typeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    navController: NavController,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val detail = uiState.detail

    // Color principal según tipo primario
    val primaryTypeColor = detail?.types?.firstOrNull()
        ?.let { typeColors[it] } ?: Color(0xFFCC0000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detail?.name?.replaceFirstChar { it.uppercaseChar() } ?: "Detalle",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryTypeColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDetail() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reintentar")
                    }
                }
            }

            detail != null -> {
                PokemonDetailContent(
                    detail = detail,
                    primaryTypeColor = primaryTypeColor,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailContent(
    detail: PokemonDetail,
    primaryTypeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con fondo de color del tipo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryTypeColor.copy(alpha = 0.15f))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = detail.spriteUrl,
                    contentDescription = detail.name,
                    modifier = Modifier.size(180.dp)
                )
                Text(
                    text = "#%03d".format(detail.id),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Chips de tipos
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    detail.types.forEach { TypeChip(type = it) }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // ── Datos físicos ──────────────────────────────────────────────
            SectionTitle("Datos físicos")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(label = "Altura", value = "%.1f m".format(detail.height / 10.0))
                StatBox(label = "Peso", value = "%.1f kg".format(detail.weight / 10.0))
                StatBox(label = "Exp. base", value = detail.baseExperience.toString())
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // ── Stats base ─────────────────────────────────────────────────
            SectionTitle("Stats base")
            Spacer(modifier = Modifier.height(8.dp))
            detail.stats.forEach { stat ->
                StatBar(stat = stat, color = primaryTypeColor)
                Spacer(modifier = Modifier.height(6.dp))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // ── Habilidades ────────────────────────────────────────────────
            if (detail.abilities.isNotEmpty()) {
                SectionTitle("Habilidades")
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    detail.abilities.forEach { ability ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = ability.replace("-", " ")
                                    .split(" ")
                                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } },
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }

            // ── Flavor text (descripción de la especie) ────────────────────
            if (detail.flavorText.isNotBlank()) {
                SectionTitle("Descripción")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${detail.flavorText}\"",
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
    )
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatBar(stat: PokemonStat, color: Color) {
    val statDisplayName = stat.name
        .split("-")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    val maxStatValue = 255f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statDisplayName,
            modifier = Modifier.width(100.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "%3d".format(stat.value),
            modifier = Modifier.width(36.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { (stat.value / maxStatValue).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
