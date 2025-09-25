package com.alexander.seriesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.alexander.seriesapp.model.Serie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SeriesViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _series = MutableStateFlow<List<Serie>>(emptyList())
    val series: StateFlow<List<Serie>> = _series

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError

    init {
        loadSeries()
    }

    // 👉 Método público que puedes usar en SwipeRefresh
    fun loadSeries() {
        _isLoading.value = true
        _hasError.value = false
        fetchSeries()
    }

    // 🔎 Encargado de traer los datos de Firestore
    private fun fetchSeries() {
        db.collection("series")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.map { doc ->
                    Serie(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        episodios = doc.getLong("episodios")?.toInt() ?: 0,
                        imagenUrl = doc.getString("imagenUrl") ?: ""
                    )
                }
                _series.value = lista
                _isLoading.value = false
            }
            .addOnFailureListener {
                _hasError.value = true
                _isLoading.value = false
            }
    }

    fun addSerie(nombre: String, episodios: Int, imagenUrl: String) {
        val nuevaSerie = hashMapOf(
            "nombre" to nombre,
            "episodios" to episodios,
            "imagenUrl" to imagenUrl
        )

        db.collection("series")
            .add(nuevaSerie)
            .addOnSuccessListener { documentRef ->
                // Se creó con ID único en Firestore
                val serie = Serie(
                    id = documentRef.id,
                    nombre = nombre,
                    episodios = episodios,
                    imagenUrl = imagenUrl
                )
                // Actualizamos el state local al toque
                _series.value = _series.value + serie
            }
            .addOnFailureListener {
                _hasError.value = true
            }
    }

    fun updateSerie(serie: Serie) {
        val serieMap = hashMapOf(
            "nombre" to serie.nombre,
            "episodios" to serie.episodios,
            "imagenUrl" to serie.imagenUrl
        )
        db.collection("series")
            .document(serie.id)
            .set(serieMap)
            .addOnSuccessListener {
                // Actualiza la lista local
                _series.value = _series.value.map {
                    if (it.id == serie.id) serie else it
                }
            }
            .addOnFailureListener {
                _hasError.value = true
            }
    }

    fun deleteSerie(serie: Serie) {
        db.collection("series")
            .document(serie.id)
            .delete()
            .addOnSuccessListener {
                _series.value = _series.value.filter { it.id != serie.id }
            }
            .addOnFailureListener {
                _hasError.value = true
            }
    }
}
