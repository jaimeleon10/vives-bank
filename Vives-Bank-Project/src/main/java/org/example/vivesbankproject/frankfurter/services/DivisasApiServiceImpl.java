package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterConnectionException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterUnexpectedException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankfurterEmptyResponseException;
import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class DivisasApiServiceImpl {

    private final DivisasApiService divisasApiService;

    public DivisasApiServiceImpl(DivisasApiService divisasApiService) {
        this.divisasApiService = divisasApiService;
    }

    public CompletableFuture<FrankFurterResponse> getLatestRatesAsync(String baseCurrency, String targetCurrencies, Double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<FrankFurterResponse> response = divisasApiService.getLatestRates(baseCurrency, targetCurrencies).execute();
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

    private void convertExchangeRates(FrankFurterResponse response, Double amount) {
        var exchangeRates = response.getExchangeRates();
        var convertedAmounts = new HashMap<String, Double>();
        exchangeRates.forEach((currency, rate) ->
                convertedAmounts.put(currency, rate * amount));
        response.setExchangeRates(convertedAmounts);
    }
}
