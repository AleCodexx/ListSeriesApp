package com.alexander.seriesapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.alexander.seriesapp.data.SessionManager
import com.alexander.seriesapp.viewmodel.SeriesViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.alexander.seriesapp.model.Serie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val seriesList = viewModel.series
    val isLoading = viewModel.isLoading
    val hasError = viewModel.hasError

    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userEmail = sessionManager.getUser() ?: "Usuario"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Series de $userEmail") },
                actions = {
                    TextButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            sessionManager.clearSession()
                            onLogout()
                        }
                    ) {
                        Text(
                            "Cerrar sesión",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.padding(padding).padding(16.dp))
            }
            hasError -> {
                Text("Error al cargar series", modifier = Modifier.padding(padding).padding(16.dp))
            }
            seriesList.isEmpty() -> {
                Text("No hay series registradas", modifier = Modifier.padding(padding).padding(16.dp))
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    items(seriesList) { serie ->
                        SerieCard(serie)
                    }
                }
            }
        }
    }
}

@Composable
fun SerieCard(serie: Serie) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Imagen de portada (usa una URL de Firebase si la tienes en tu modelo)
            Image(
                painter = rememberAsyncImagePainter(
                    serie.imagenUrl ?: "https://via.placeholder.com/300x400.png?text=No+Image"
                ),
                contentDescription = serie.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = serie.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "${serie.episodios} episodios",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
