package com.ebc.crud_basico.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * ApiClient es el punto central para crear y exponer la instancia de Retrofit
 * que se usará para consumir el servicio de frases geek.
 */
object ApiClient {
    // URL base del backend donde vive tu servicio de Spring Boot.
    // Ojo: debe terminar en "/" para que Retrofit concatene bien los paths.
    private const val BASE_URL = "https://loteriasvarias.onrender.com/"

    /**
     * Instancia de la interfaz GeekQuoteApi.
     *
     * - Se crea de forma "lazy": solo se inicializa la primera vez que se usa.
     * - A partir de aquí puedes llamar a los métodos definidos en GeekQuoteApi
     *   (por ejemplo, getRandomQuote()) sin preocuparte por configurar Retrofit cada vez.
     */
    val geekQuoteApi: GeekQuoteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GeekQuoteApi::class.java)
    }
}