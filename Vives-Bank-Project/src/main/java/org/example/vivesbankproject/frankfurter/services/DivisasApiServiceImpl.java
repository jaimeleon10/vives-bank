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

@Service
public class DivisasApiServiceImpl implements DivisasApiService{

    private final DivisasApiService apiClient;

    public DivisasApiServiceImpl(Retrofit retrofit) {
        this.apiClient = retrofit.create(DivisasApiService.class);
    }

    @Override
    public Call<FrankFurterResponse> getLatestRates(String base, String symbols) {
        return apiClient.getLatestRates(base, symbols);
    }

    public CompletableFuture<FrankFurterResponse> getLatestRatesAsync(String baseCurrency, String targetCurrencies, String amount) {
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

    private void convertExchangeRates(FrankFurterResponse response, String amount) {
        var exchangeRates = response.getExchangeRates();
        var convertedAmounts = new HashMap<String, Double>();
        exchangeRates.forEach((currency, rate) ->
                convertedAmounts.put(currency, rate * Double.parseDouble(amount)));
        response.setExchangeRates(convertedAmounts);
        response.setAmount(amount);
    }
}