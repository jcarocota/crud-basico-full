package com.ebc.crud_basico.core

// Esta data class representa el "estado" de un TextField.
// La idea es no manejar solo un String suelto, sino un objeto que pueda crecer
// (por ejemplo, agregar error, etiqueta, helperText, etc. más adelante).
data class TextFieldState(
    // Valor actual del texto que se muestra en el TextField.
    // Por defecto es una cadena vacía.
    val text: String = ""
)
