package com.ebc.crud_basico.network

import retrofit2.http.GET

/**
 * Interfaz que define los endpoints del servicio de frases geek.
 *
 * Retrofit genera la implementación de esta interfaz en tiempo de ejecución.
 * Tú solo declaras:
 *  - qué método HTTP usas (@GET, @POST, etc.)
 *  - a qué ruta le pegas ("quotes")
 *  - y qué tipo de dato esperas de respuesta (String en este caso).
 */
interface GeekQuoteApi {

    /**
     * Llama al endpoint GET /quotes del backend.
     *
     * - @GET("quotes") indica que es una petición HTTP GET a la ruta "quotes"
     *   relativa al BASE_URL definido en ApiClient.
     *   Ejemplo completo: https://loteriasvarias.onrender.com/quotes
     *
     * - 'suspend' permite que esta función se ejecute dentro de una corrutina
     *   sin bloquear el hilo principal (ideal para llamadas de red).
     *
     * - Regresa un String porque el backend responde solo con texto plano
     *   (la frase geek motivacional).
     */
    @GET("quotes")
    suspend fun getRandomQuote(): String
}