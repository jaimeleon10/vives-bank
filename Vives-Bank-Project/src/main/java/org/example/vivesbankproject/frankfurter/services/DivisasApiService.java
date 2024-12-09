package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz para definir el cliente Retrofit utilizado para interactuar con la API de tasas de cambio de Frankfurter.
 * Contiene la definición de la operación para obtener las últimas tasas de cambio.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
public interface DivisasApiService {

    /**
     * Obtiene las últimas tasas de cambio desde la base de una moneda a un conjunto de monedas destino.
     *
     * @param base   La moneda base desde la cual se realizarán las conversiones (por ejemplo, EUR).
     * @param symbols Una lista separada por comas de los símbolos de moneda destino para las tasas de cambio (por ejemplo, USD,GBP).
     * @return Un objeto {@link Call} que contiene la respuesta de las tasas de cambio en formato {@link FrankFurterResponse}.
     */
    @GET("/latest")
    Call<FrankFurterResponse> getLatestRates(
            @Query("base") String base,
            @Query("symbols") String symbols
    );
}