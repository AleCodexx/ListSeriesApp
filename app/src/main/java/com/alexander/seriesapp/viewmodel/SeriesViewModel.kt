package com.alexander.seriesapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alexander.seriesapp.model.Serie
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class SeriesViewModel : ViewModel() {
    val series = mutableStateListOf<Serie>()
    var isLoading by mutableStateOf(false)
    var hasError by mutableStateOf(false)

    private val database = Firebase.database
    private val seriesRef = database.getReference("series")

    init {
        loadSeries()
    }

    private fun loadSeries() {
        isLoading = true

        seriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Serie>()
                for (item in snapshot.children) {
                    val serie = item.getValue(Serie::class.java)
                    serie?.let { tempList.add(it) }
                }

                series.clear()
                series.addAll(tempList)
                isLoading = false

                Log.d("SeriesApp", "Series cargadas: ${series.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SeriesApp", "Error al leer series", error.toException())
                hasError = true
                isLoading = false
            }
        })
    }

    fun agregarSerie(nombre: String, episodios: Int, imagenUrl: String? = null) {
        val record = seriesRef.push()
        val serie = Serie(id = record.key, nombre = nombre, episodios = episodios, imagenUrl = imagenUrl)
        record.setValue(serie)
    }

}