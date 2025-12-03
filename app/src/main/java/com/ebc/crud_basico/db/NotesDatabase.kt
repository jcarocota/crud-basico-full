package com.ebc.crud_basico.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ebc.crud_basico.db.model.Note

// @Database indica que esta clase representa la base de datos de Room.
// - entities: lista de entidades (tablas) que manejará la BD.
// - version: versión de la BD (se incrementa cuando cambias el esquema).
// - exportSchema: si es false, Room no genera un archivo de esquema (útil para evitar
//   archivos extra en proyectos pequeños o de ejemplo).
@Database(entities = [(Note::class)], version = 2, exportSchema = false)

// @TypeConverters indica que esta BD usará la clase Converters para convertir tipos
// que Room no puede guardar directamente (por ejemplo, Date <-> Long).
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase(){

    // Esta función abstracta le dice a Room que debe generar la implementación
    // del DAO (NotesDao) y here te da acceso a él.
    abstract fun notesDao(): NotesDao

    // Usamos un companion object para implementar el patrón Singleton:
    // solo habrá UNA instancia de NotesDatabase en toda la app.
    companion object {

        // @Volatile garantiza que cualquier hilo siempre vea el valor más reciente de INSTANCE.
        // Es importante en entornos multi-hilo para evitar instancias duplicadas.
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        // Esta función devuelve la instancia única de la base de datos.
        // Si no existe, la crea; si ya existe, regresa la misma.
        fun getInstance(context: Context): NotesDatabase {

            // synchronized bloquea este bloque para que solo un hilo a la vez pueda ejecutarlo.
            // Esto evita que dos hilos creen dos instancias al mismo tiempo.
            synchronized(this) {
                // Primero leemos la instancia actual
                var instance = INSTANCE

                // Si es null, significa que aún no se ha creado la BD
                if (instance == null) {

                    instance = Room.databaseBuilder(
                        // applicationContext para evitar fugas de memoria (no usar context de Activity)
                        context.applicationContext,
                        // Clase de la BD
                        NotesDatabase::class.java,
                        // Nombre del archivo físico de la base de datos
                        "notes_database"
                    )
                        // fallbackToDestructiveMigration:
                        // Si cambias la versión y no defines una migración,
                        // Room BORRA y RECREA la BD (pierdes datos).
                        // Útil para ejemplos, demos o durante desarrollo.
                        .fallbackToDestructiveMigration()
                        .build()

                    // Guardamos la instancia creada en la variable estática
                    INSTANCE = instance
                }
                // Devolvemos la instancia (nueva o ya existente)
                return instance
            }
        }
    }
}