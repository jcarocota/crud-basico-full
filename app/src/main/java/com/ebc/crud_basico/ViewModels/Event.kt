package com.ebc.crud_basico.ViewModels

// Una sealed class (clase sellada) se usa para representar un conjunto cerrado de "tipos de eventos".
// Aquí estás definiendo todos los eventos que la pantalla puede enviar al ViewModel.
// La ventaja es que cuando haces un `when(event)` en el ViewModel, el compilador sabe
// todos los casos posibles y te obliga a manejarlos (o a usar else).
sealed class Event {
    // Evento para actualizar un texto (por ejemplo, el contenido de un TextField).
    // Lleva un parámetro `text` con el nuevo valor.
    data class SetText(val text: String): Event()

    // Evento para indicar que se debe abrir un diálogo (por ejemplo, para crear/editar una nota).
    object OpenDialog: Event()

    // Evento para indicar que se debe cerrar el diálogo.
    object CloseDialog: Event()

    // Evento para indicar que se debe guardar la nota actual.
    // Normalmente el ViewModel, al recibir este evento, valida datos y llama al repositorio.
    object Save: Event()

    // Evento para eliminar una nota. Lleva el id opcional de la nota a eliminar.
    // El id es Int? (nullable) por si en algún momento no se tiene el id disponible,
    // aunque en un caso real normalmente debería venir no null.
    data class Delete(val id: Int?): Event()

    // Evento para cargar una nota desde la BD (por ejemplo, al abrir pantalla de edición).
    // También recibe un id opcional.
    data class Load(val id: Int?): Event()

    data class SetImagePath(val imagePath: String?): Event()

    data class FireQuote(val quote: String?): Event()
}