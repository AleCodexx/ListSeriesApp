package com.alexander.seriesapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alexander.seriesapp.model.Serie
import com.alexander.seriesapp.viewmodel.SeriesViewModel
import androidx.compose.ui.text.input.KeyboardType
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    viewModel: SeriesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogout: () -> Unit
) {
    val seriesList by viewModel.series.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasError by viewModel.hasError.collectAsState()

    // 👉 Estados para el diálogo
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var episodios by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    // Estado para el diálogo de detalles
    var selectedSerie by remember { mutableStateOf<Serie?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Series") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = { viewModel.loadSeries() },
            modifier = Modifier.padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                hasError -> Text("Error al cargar series", modifier = Modifier.padding(16.dp))
                seriesList.isEmpty() -> Text("No hay series registradas", modifier = Modifier.padding(16.dp))
                else -> LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(seriesList) { serie ->
                        SerieCard(
                            serie = serie,
                            onClick = {
                                selectedSerie = serie
                                showDetailDialog = true
                            }
                        )
                    }
                }
            }
        }

        // 👉 Diálogo para añadir nueva serie
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (selectedSerie == null) "Añadir nueva serie" else "Editar serie") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de la serie") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = episodios,
                            onValueChange = { episodios = it },
                            label = { Text("Número de episodios") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = imagenUrl,
                            onValueChange = { imagenUrl = it },
                            label = { Text("URL de la imagen") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (nombre.isNotBlank() && episodios.isNotBlank()) {
                            if (selectedSerie == null) {
                                viewModel.addSerie(
                                    nombre = nombre,
                                    episodios = episodios.toIntOrNull() ?: 0,
                                    imagenUrl = imagenUrl.ifBlank {
                                        "https://via.placeholder.com/300x400.png?text=No+Image"
                                    }
                                )
                            } else {
                                viewModel.updateSerie(
                                    selectedSerie!!.copy(
                                        nombre = nombre,
                                        episodios = episodios.toIntOrNull() ?: 0,
                                        imagenUrl = imagenUrl
                                    )
                                )
                            }
                            // Limpiar y cerrar
                            nombre = ""
                            episodios = ""
                            imagenUrl = ""
                            selectedSerie = null
                            showDialog = false
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        selectedSerie = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // 👉 Diálogo de detalles de serie
        if (showDetailDialog && selectedSerie != null) {
            AlertDialog(
                onDismissRequest = { showDetailDialog = false },
                title = { Text(selectedSerie!!.nombre) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedSerie!!.imagenUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = selectedSerie!!.nombre,
                            contentScale = ContentScale.Fit,
                            placeholder = painterResource(android.R.drawable.ic_menu_report_image),
                            error = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            modifier = Modifier
                                .height(140.dp)
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Episodios: ${selectedSerie!!.episodios}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Acción de editar
                        nombre = selectedSerie!!.nombre
                        episodios = selectedSerie!!.episodios.toString()
                        imagenUrl = selectedSerie!!.imagenUrl
                        showDialog = true
                        showDetailDialog = false
                    }) {
                        Text("Editar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDetailDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }
    }
}

@Composable
fun SerieCard(serie: Serie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Imagen a la izquierda
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(serie.imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = serie.nombre,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(android.R.drawable.ic_menu_report_image),
                error = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(3f / 4f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info de la serie a la derecha
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = androidx.compose.ui.Alignment.CenterVertically)
            ) {
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
