package com.alexander.seriesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import com.alexander.seriesapp.ui.SeriesScreen
import com.alexander.seriesapp.viewmodel.SeriesViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SeriesViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SeriesApp(viewModel)
            }
        }

        /*
        viewModel.agregarSerie("Loki", 12)
        viewModel.agregarSerie("Breaking Bad", 62)
        viewModel.agregarSerie("Stranger Things", 34)
        viewModel.agregarSerie("The Office", 201)
        viewModel.agregarSerie("The Boys", 24)
        viewModel.agregarSerie("Game of Thrones", 73)
        viewModel.agregarSerie("Friends", 236)
        viewModel.agregarSerie("The Mandalorian", 16)
        viewModel.agregarSerie("The Witcher", 16)*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesApp(viewModel: SeriesViewModel) {
    Scaffold (
        topBar = { TopAppBar(title = { Text("SERIES") }) }
    ) { padding ->
        when {
            viewModel.hasError -> Text("Error al cargar datos", modifier = Modifier.padding(padding))
            viewModel.isLoading -> CircularProgressIndicator(modifier = Modifier.padding(padding))
            else -> SeriesScreen(
                seriesList = viewModel.series,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
