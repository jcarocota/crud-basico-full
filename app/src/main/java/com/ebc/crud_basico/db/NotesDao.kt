package com.ebc.crud_basico.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ebc.crud_basico.db.model.Note

// Esta interfaz define las operaciones (métodos) que se pueden hacer sobre la tabla "notes".
// Room genera la implementación real en tiempo de compilación.
@Dao
interface NotesDao {

    // Inserta una nueva nota en la tabla "notes".
    // Si la entidad Note tiene un @PrimaryKey autoGenerate, Room se encarga de asignar el id.
    @Insert
    fun insert(note: Note)

    // Actualiza una nota existente en la base de datos.
    // Para que funcione, el Note debe tener el mismo id que ya existe en la tabla.
    @Update
    fun update(note: Note)

    // Elimina una nota filtrando por id.
    // El parámetro :id es reemplazado por el valor del parámetro de la función.
    @Query("DELETE FROM notes WHERE id = :id")
    fun delete(id: Int)

    // Obtiene todas las notas de la tabla "notes".
    // LiveData permite observar los cambios: cuando se inserta, actualiza o borra una nota,
    // los observadores de este LiveData se vuelven a ejecutar automáticamente.
    @Query("SELECT * FROM notes")
    fun all(): LiveData<List<Note>>

    // Busca una nota por id y la regresa como un objeto Note.
    // La función es suspend, lo que significa que se llama desde una corrutina
    // o desde otra función suspend (por ejemplo, dentro de un ViewModel con viewModelScope.launch).
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun findById(id: Int): Note

}