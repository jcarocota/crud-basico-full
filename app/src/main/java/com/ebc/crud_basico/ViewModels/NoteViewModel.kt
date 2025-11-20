package com.ebc.crud_basico.ViewModels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ebc.crud_basico.core.TextFieldState
import com.ebc.crud_basico.db.NotesDatabase
import com.ebc.crud_basico.db.model.Note
import com.ebc.crud_basico.repository.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

// ViewModel que maneja el estado y la lógica de la pantalla de notas.
// Recibe un Application para poder obtener la instancia de la base de datos.
class NoteViewModel(application: Application): ViewModel() {

    // Repositorio que encapsula el acceso a la BD (a través del DAO).
    private val repository: NotesRepository

    // Scope de corrutinas propio, además del viewModelScope.
    // Aquí lo usas para emitir eventos y ejecutar operaciones en segundo plano.
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // MutableSharedFlow es un flujo de eventos “one-shot” (para cosas como mostrar Snackbars,
    // navegar, etc.). Es similar a un canal de eventos.
    private val _eventFlow = MutableSharedFlow<Event>()
    // Expones una versión de solo lectura del flujo hacia la UI.
    val eventFlow = _eventFlow.asSharedFlow()

    // Estado interno del TextField (texto, error, etc.).
    // _text es mutable y privado, text es solo lectura para la UI.
    private val _text = mutableStateOf(TextFieldState())
    val text: State<TextFieldState> = _text


    // LiveData con la lista de todas las notas almacenadas en la BD.
    // Se puede observar desde la UI (o desde un composable con observeAsState).
    val all: LiveData<List<Note>>

    // Estado que controla si el diálogo de edición/creación está abierto.
    var openDialog by mutableStateOf(false)

    // Guarda el id de la nota que actualmente se está editando.
    // Si es null, significa que estamos creando una nueva nota.
    private var currentId: Int? = null

    // Bloque init: se ejecuta cuando se crea el ViewModel.
    init {
        // Obtenemos la instancia de la base de datos de Room.
        val db = NotesDatabase.getInstance(application)
        // Obtenemos el DAO desde la BD.
        val dao = db.notesDao()
        // Creamos el repositorio usando el DAO.
        repository = NotesRepository(dao)

        // Cargamos todas las notas. Esto es un LiveData que se actualizará automáticamente
        // si cambian los datos en la base.
        all = repository.all()
    }

    // Carga una nota desde la BD dado su id.
    // Si id es null, prepara el estado para una nueva nota.
    private fun load(id: Int?){
        // viewModelScope se cancela automáticamente cuando el ViewModel se destruye.
        viewModelScope.launch {
            if (id != null) {
                // findById es suspend, por eso lo llamamos dentro de una corrutina.
                repository.findById(id).also { note ->
                    // Guardamos el id actual para saber si estamos editando o creando.
                    currentId = note.id
                    // Actualizamos el estado del TextField con el texto de la nota.
                    _text.value = text.value.copy(
                        text = note.text
                    )
                }
            }else{
                // Si id es null, significa que vamos a crear una nota nueva.
                currentId = null
                _text.value = text.value.copy(
                    text = "text" // valor por defecto inicial (podrías dejarlo vacío "")
                )
            }
        }
    }

    // Función central que recibe todos los eventos de la UI.
    // Aquí decides qué hacer dependiendo del tipo de Event.
    fun onEvent(event: Event) {
        when (event) {
            is Event.SetText -> {
                // Actualiza el texto del TextField cuando el usuario escribe.
                _text.value = text.value.copy(
                    text = event.text
                )
            }
            is Event.Save -> {
                // Si currentId NO es null, estamos editando una nota existente.
                if(currentId != null){
                    // Actualiza la nota en la BD.
                    repository.update(Note(currentId, text.value.text, Date()))
                }else{
                    // Si currentId es null, estamos creando una nueva nota.
                    repository.insert(Note(null, text.value.text, Date()))
                }
                // Cierra el diálogo después de guardar.
                openDialog = false

                // Emitimos un evento Save a través del SharedFlow.
                // Esto puede usarse en la UI para mostrar un mensaje, navegar, etc.
                coroutineScope.launch(Dispatchers.IO) {
                    _eventFlow.emit(Event.Save)
                }
            }
            is Event.OpenDialog -> {
                // Abre el diálogo (sin cargar datos específicos).
                openDialog = true
            }
            is Event.CloseDialog -> {
                // Cierra el diálogo sin guardar.
                openDialog = false
            }
            is Event.Load -> {
                // Carga una nota por id (para editarla).
                load(event.id)
                // Abre el diálogo de edición/creación.
                openDialog = true
            }
            is Event.Delete -> {
                // Si el id no es null, elimina la nota.
                event.id?.let { repository.delete(it) }
            }
        }
    }
}