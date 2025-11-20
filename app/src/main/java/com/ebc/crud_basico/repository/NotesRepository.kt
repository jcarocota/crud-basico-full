package com.ebc.crud_basico.repository

import androidx.lifecycle.LiveData
import com.ebc.crud_basico.db.NotesDao
import com.ebc.crud_basico.db.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// El repositorio actúa como una capa intermedia entre el ViewModel (o la UI)
// y el DAO. Aquí decides:
// - En qué hilo se ejecutan las operaciones.
// - Si combinas datos de varias fuentes (BD local, red, etc.).
class NotesRepository(private  val notesDao: NotesDao) {

    // Scope de corrutinas para lanzar operaciones de BD.
    // Se crea con Dispatchers.Main, pero en cada launch especificas Dispatchers.IO.
    // Podrías usar directamente viewModelScope en el ViewModel, pero para demo está bien.
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Inserta una nueva nota en la base de datos.
    // Se ejecuta en un hilo de I/O para no bloquear el hilo principal (UI).
    fun insert(note: Note) {
        coroutineScope.launch (Dispatchers.IO) {
            notesDao.insert(note)
        }
    }

    // Actualiza una nota existente.
    // Igual que insert, va en Dispatchers.IO.
    fun update(note: Note) {
        coroutineScope.launch(Dispatchers.IO) {
            notesDao.update(note)
        }
    }

    // Devuelve todas las notas como LiveData.
    // No se necesita corrutina aquí porque Room ya maneja esto de manera reactiva.
    // La consulta se ejecuta en un hilo adecuado internamente.
    fun all(): LiveData<List<Note>> {
        return notesDao.all()
    }

    // Busca una nota por id.
    // Este método es suspend, así que debe llamarse dentro de una corrutina
    // (por ejemplo, desde un ViewModel usando viewModelScope.launch).
    suspend fun findById(id: Int): Note {
        return notesDao.findById(id)
    }

    // Elimina una nota por id.
    // También se lanza en un hilo de I/O para no bloquear la UI.
    fun delete(id: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            notesDao.delete(id)
        }
    }
}