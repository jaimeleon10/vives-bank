package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterConnectionException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterUnexpectedException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankfurterEmptyResponseException;
import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.HashMap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Implementación del servicio para interactuar con la API de tasas de cambio Frankfurter.
 * Proporciona operaciones para obtener las últimas tasas de cambio de forma síncrona y asíncrona.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
public class DivisasApiServiceImpl implements DivisasApiService {

    private final DivisasApiService apiClient;

    /**
     * Constructor para inicializar el cliente Retrofit con la interfaz DivisasApiService.
     *
     * @param retrofit Retrofit para la configuración y llamadas a la API externa.
     */
    public DivisasApiServiceImpl(Retrofit retrofit) {
        this.apiClient = retrofit.create(DivisasApiService.class);
    }

    /**
     * Obtiene las últimas tasas de cambio de manera síncrona.
     *
     * @param base   Moneda base para la consulta (por ejemplo, EUR).
     * @param symbols Monedas destino a las que se desea obtener la tasa de cambio separadas por comas (por ejemplo, USD,GBP).
     * @return Un objeto {@link Call} con la respuesta de las tasas de cambio.
     */
    @Override
    @Operation(summary = "Obtener últimas tasas de cambio",
            description = "Obtiene las últimas tasas de cambio para las monedas proporcionadas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Consulta exitosa",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FrankFurterResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
            })
    public Call<FrankFurterResponse> getLatestRates(@Parameter(description = "Moneda base", example = "EUR") String base,
                                                    @Parameter(description = "Monedas objetivo separadas por comas", example = "USD,GBP") String symbols) {
        return apiClient.getLatestRates(base, symbols);
    }

    /**
     * Obtiene las últimas tasas de cambio de forma asíncrona.
     * Convierte las tasas de cambio utilizando la cantidad proporcionada.
     *
     * @param baseCurrency      Moneda base para la consulta (por ejemplo, EUR).
     * @param targetCurrencies  Monedas destino a las que se desea obtener la tasa de cambio separadas por comas.
     * @param amount            Cantidad para convertir las tasas de cambio.
     * @return Un objeto {@link CompletableFuture} con la respuesta de la operación asíncrona.
     */
    @Operation(summary = "Obtener tasas de cambio de forma asíncrona",
            description = "Obtiene las tasas de cambio de forma asíncrona y convierte las tasas a partir de una cantidad específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Consulta exitosa",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FrankFurterResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Error en el servidor"),
                    @ApiResponse(responseCode = "404", description = "Divisas no encontradas"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
            })
    public CompletableFuture<FrankFurterResponse> getLatestRatesAsync(
            @Parameter(description = "Moneda base para la consulta", example = "EUR") String baseCurrency,
            @Parameter(description = "Monedas destino a consultar separadas por comas", example = "USD,GBP") String targetCurrencies,
            @Parameter(description = "Cantidad para realizar la conversión", example = "10") String amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<FrankFurterResponse> response = getLatestRates(baseCurrency, targetCurrencies).execute();
                if (!response.isSuccessful()) {
                    throw new FrankFurterUnexpectedException(baseCurrency, targetCurrencies);
                }
                FrankFurterResponse result = response.body();
                if (result == null) {
                    throw new FrankfurterEmptyResponseException(baseCurrency, targetCurrencies, amount);
                }
                convertExchangeRates(result, amount);
                return result;
            } catch (IOException e) {
                throw new FrankFurterConnectionException(baseCurrency, targetCurrencies, e);
            }
        });
    }

    /**
     * Convierte las tasas de cambio utilizando la cantidad proporcionada por el usuario.
     *
     * @param response Objeto {@link FrankFurterResponse} que contiene las tasas de cambio.
     * @param amount   Cantidad proporcionada para realizar la conversión.
     */
    private void convertExchangeRates(FrankFurterResponse response, String amount) {
        var exchangeRates = response.getExchangeRates();
        var convertedAmounts = new HashMap<String, Double>();
        exchangeRates.forEach((currency, rate) ->
                convertedAmounts.put(currency, rate * Double.parseDouble(amount)));
        response.setExchangeRates(convertedAmounts);
        response.setAmount(amount);
    }
}