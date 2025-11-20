package com.ebc.crud_basico.db

import androidx.room.TypeConverter
import java.util.Date


// Esta clase se usa para decirle a Room cómo convertir tipos que NO sabe guardar directamente
// (como java.util.Date) a tipos que SÍ puede guardar en la base de datos (como Long) y viceversa.
class Converters {

    // Este método convierte un Long (timestamp en milisegundos) a un objeto Date.
    // Room va a usar este método cuando LEA de la base de datos y necesite un Date en tu entidad.
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        // Si value es null, regresa null.
        // Si no es null, crea un nuevo Date usando ese timestamp (milisegundos desde 1/1/1970).
        return value?.let { Date(it) }
    }

    // Este método convierte un Date a Long (timestamp en milisegundos).
    // Room va a usar este método cuando ESCRIBA en la base de datos un campo de tipo Date.
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        // Si date es null, regresa null.
        // Si no es null, obtenemos los milisegundos de ese Date con .time
        // y lo regresamos como Long.
        return date?.time?.toLong()
    }
}