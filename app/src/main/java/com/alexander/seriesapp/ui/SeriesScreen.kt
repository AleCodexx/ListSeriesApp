package com.alexander.seriesapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexander.seriesapp.model.Serie

@Composable
fun SeriesScreen(seriesList: List<Serie>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(seriesList) { serie ->
            SerieCard(serie)
        }
    }
}

@Composable
fun SerieCard(serie: Serie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Surface(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            ) {}

            Text(
                text = serie.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = "Episodios: ${serie.episodios}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}
