package com.ebc.crud_basico.ViewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Esta clase es una "fábrica" de ViewModels.
// Sirve cuando tu ViewModel necesita parámetros en el constructor
// (por ejemplo, Application, repository, id, etc.) y no puedes crearlo
// con el constructor vacío que usa Android por defecto.
class NoteViewModelFactory (private val application: Application) :
    ViewModelProvider.Factory {
        // Este método se llama cuando alguien (Activity, Fragment, etc.)
        // pide una instancia de un ViewModel usando este Factory.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Aquí tú decides cómo crear el ViewModel.
            // En este caso siempre regresamos un NoteViewModel pasando el Application.
            // El "as T" es un cast genérico porque el método debe regresar un ViewModel de tipo T.
        return NoteViewModel(application) as T
    }
}