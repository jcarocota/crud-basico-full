package com.ebc.crud_basico

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ebc.crud_basico.ViewModels.Event
import com.ebc.crud_basico.ViewModels.NoteViewModel
import com.ebc.crud_basico.ViewModels.NoteViewModelFactory
import com.ebc.crud_basico.db.model.Note
import com.ebc.crud_basico.ui.theme.CrudbasicoTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Activity principal de la app. Es el punto de entrada en Android.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activa modo edge-to-edge (contenido por debajo de barras del sistema).
        enableEdgeToEdge()
        // setContent define el contenido de la pantalla usando Jetpack Compose.
        setContent {
            CrudbasicoTheme {
                // Surface es un contenedor que aplica el color de fondo del tema.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Crud()
                }
            }
        }
    }
}

@Composable
fun Crud() {
    // Obtiene el ViewModelStoreOwner (normalmente la Activity).
    // LocalViewModelStoreOwner es un CompositionLocal que te da el objeto que “posee” los ViewModels.
    //
    //En tu caso, como estás dentro del setContent { ... } de la MainActivity, el owner es la Activity.
    //
    //O sea: owner ≈ “la entidad que guarda y administra los ViewModels”.
    val owner = LocalViewModelStoreOwner.current

    /*
    Context es como el “entorno” o “referencia al mundo de Android” que te permite:

        - Saber quién eres (qué app / componente está ejecutando código).
        - Acceder a recursos (strings, colores, layouts, etc.).
        - Iniciar otras actividades, mostrar toasts, diálogos “clásicos”, etc.
        - Acceder a servicios del sistema (vibración, notificaciones, etc.).
        - Llegar a cosas como SharedPreferences, ContentResolver, etc.
     */


    //“Si hay un owner, entonces crea el ViewModel y la UI. Si no, no hagas nada”.
    owner?.let {
        // Crea/obtiene el NoteViewModel usando la Factory,
        // porque el ViewModel recibe un Application como parámetro.
        val viewModel: NoteViewModel = viewModel(
            it, //l ViewModelStoreOwner (tu Activity).
            "NoteViewModel", //la key del ViewModel. Sirve para diferenciar varios ViewModels del mismo tipo asociados al mismo owner (no es obligatorio, pero es válido).
            NoteViewModelFactory(//NoteViewModelFactory(...) -> la Factory que sabe cómo crear NoteViewModel.
                // LocalContext.current → te da el Context actual del composable.
                //.applicationContext → sube al contexto de aplicación (no al de Activity).
                //as Application → lo casteas a Application.
                LocalContext.current.applicationContext
                        as Application
            )
        )

        // Llama a la pantalla principal pasándole el ViewModel.
        CrudScreenSetup(viewModel)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudScreenSetup(viewModel: NoteViewModel) {

    // Observa el LiveData de notas como estado de Compose.
    // Cuando cambian las notas en la BD, este valor se actualiza y dispara recomposición.
    val allNotes by viewModel.all.observeAsState(listOf())

    // Estado para el Snackbar (contiene la cola de mensajes).
    /*
    Snackbar es, básicamente, un mensajito temporal que aparece en la parte de abajo de la pantalla para avisarle algo al usuario, a veces con un botón de acción.

        - Piensa en cosas como:
        - “Nota guardada”
        - “Elemento eliminado – DESHACER”
        - “Sin conexión a internet”
        - No bloquea la pantalla, no es un diálogo. Sale, se muestra unos segundos y se va.
     */
    val snackbarHostState = remember { SnackbarHostState() }
    // Scope de corrutinas para lanzar tareas desde composables (por ejemplo, mostrar snackbar).
    val scope = rememberCoroutineScope()

    // LaunchedEffect se ejecuta una vez cuando este composable entra en composición.
    // Aquí nos suscribimos al flujo de eventos del ViewModel.
    LaunchedEffect(snackbarHostState) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is Event.Save -> {
                    // Cuando se reciba un evento Save, mostramos un Snackbar.
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Action done"
                        )
                    }
                }

                Event.CloseDialog -> TODO()
                is Event.Delete -> TODO()
                is Event.Load -> TODO()
                Event.OpenDialog -> TODO()
                is Event.SetText -> TODO()
            }
        }
    }

    // Scaffold proporciona una estructura estándar de pantalla:
    // FAB, Snackbar, TopBar, BottomBar, etc.
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Cuando se presiona el FAB, se manda un evento Load(null)
                // para indicar que se quiere crear una nueva nota (sin id).
                viewModel.onEvent(Event.Load(null))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New note"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        // Llamamos a la pantalla principal de CRUD, pasándole:
        // - lista de notas,
        // - estado de diálogo,
        // - texto actual,
        // - y función para enviar eventos al ViewModel.
        CrudScreen(
            allNotes = allNotes,
            openDialog = viewModel.openDialog,
            text = viewModel.text.value.text,
            onEvent = { viewModel.onEvent(it) }
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudScreen(
    allNotes: List<Note>,
    openDialog: Boolean,
    text: String,
    onEvent: (Event)-> Unit,
) {
    // Contenedor principal de la pantalla.
    Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        // Lista perezosa de notas (solo renderiza los elementos visibles).
        /*
        ListItem(
            leadingContent = { ... },      // al inicio (izquierda)
            headlineContent = { ... },     // texto principal
            supportingContent = { ... },   // texto secundario
            overlineContent = { ... },     // texto arriba del principal (opcional)
            trailingContent = { ... },     // al final (derecha)
        )
         */
        LazyColumn {
            items(allNotes){
                // ListItem de Material3 para mostrar cada nota.
                ListItem(
                    headlineContent = { Text(it.text) },
                    supportingContent = { Text(it.update.toString()) },
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp),
                    trailingContent = {
                        // Botón de borrado.
                        IconButton(onClick = {
                            onEvent(Event.Delete(it.id))
                        }){
                            Icon( Icons.Rounded.Delete, contentDescription = null)
                        }
                    },
                    leadingContent = {
                        // Botón de edición.
                        IconButton(onClick = {
                            onEvent(Event.Load(it.id))
                        }){
                            Icon( Icons.Rounded.Edit, contentDescription = null)
                        }
                    }
                )
                // Divider entre elementos de la lista.
                HorizontalDivider()
            }
        }
    }

    // Diálogo de edición/creación de nota.
    EditDialog(openDialog = openDialog, text = text, onEvent = onEvent)
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditDialog(openDialog: Boolean, text: String, onEvent: (Event)-> Unit){

    //val keyboardController = LocalSoftwareKeyboardController.current

    // Solo muestra el diálogo si openDialog es true.
    if (openDialog) {
        Dialog (
            // Si se cierra el diálogo tocando fuera o atrás, se envía evento CloseDialog.
            onDismissRequest = { onEvent(Event.CloseDialog) },
            // usePlatformDefaultWidth = false hace que el diálogo pueda usar más ancho (full screen-like).
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // Surface que contendrá el contenido del diálogo.
            Surface(modifier = Modifier.fillMaxSize()) {
                // Columna con el TextField.
                Column(modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    // Campo de texto para editar el contenido de la nota.
                    TextField(
                        value = text,
                        onValueChange = { onEvent(Event.SetText(it)) },
                        label = { Text("Text") },
                        // 👇 Esto pide un botón de acción tipo "Done" en el teclado
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),

                        /*
                        keyboardActions = KeyboardActions (
                            onDone = {
                                // Aquí decides qué pasa cuando el usuario toca el ✔️
                                onEvent(Event.Save)      // por ejemplo, guardas
                                keyboardController?.hide() // 👈 cierras el teclado
                            }
                        )
                        */

                    )
                }

                // Box para colocar los botones en la parte inferior.
                Box(modifier = Modifier.padding(16.dp)) {
                    // Botón de Cancelar.
                    TextButton(
                        onClick = { onEvent(Event.CloseDialog) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text("Cancel")
                    }
                    // Botón de Confirmar/Guardar.
                    TextButton(
                        onClick = { onEvent(Event.Save) },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CrudbasicoTheme {
        Crud()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CrudbasicoTheme {
        Greeting("Android")
    }
}