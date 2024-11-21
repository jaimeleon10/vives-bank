package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;

import java.util.concurrent.CompletableFuture;

@Service
public class DivisasApiServiceImpl {

    private final DivisasApiService divisasApiService;

    public DivisasApiServiceImpl(DivisasApiService divisasApiService) {
        this.divisasApiService = divisasApiService;
    }

    public CompletableFuture<FrankFurterResponse> getLatestRatesAsync(String baseCurrency, String targetCurrencies, int amount) {
        CompletableFuture<FrankFurterResponse> future = new CompletableFuture<>();

        Call<FrankFurterResponse> call = divisasApiService.getLatestRates(baseCurrency, targetCurrencies);
        call.enqueue(new Callback<FrankFurterResponse>() {
            @Override
            public void onResponse(Call<FrankFurterResponse> call, Response<FrankFurterResponse> response) {
                if (response.isSuccessful()) {
                    var exchangeRates = response.body().getExchangeRates();
                    var convertedAmounts = new HashMap<String, Double>();

                    exchangeRates.forEach((currency, rate) -> {
                        double amountDouble = rate * amount;
                        convertedAmounts.put(currency, amountDouble);
                    });

                    response.body().setExchangeRates(convertedAmounts);
                    future.complete(response.body());
                } else {
                    String errorMessage = "Error desconocido";
                    try {
                        errorMessage = response.errorBody() != null ? response.errorBody().string() : errorMessage;
                    } catch (IOException e) {
                        // Manejar la excepción si no se puede leer el cuerpo del error
                    }
                    future.completeExceptionally(new IOException("Error fetching latest rates: " + errorMessage));
                }
            }

            @Override
            public void onFailure(Call<FrankFurterResponse> call, Throwable t) {
                future.completeExceptionally(new IOException("Failed to fetch latest rates", t));
            }
        });

        return future;
    }
}
