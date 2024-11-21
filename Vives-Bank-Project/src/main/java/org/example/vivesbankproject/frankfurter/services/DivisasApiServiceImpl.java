package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;

@Service
public class DivisasApiServiceImpl  {

    private final DivisasApiService divisasApiService;

    public DivisasApiServiceImpl(DivisasApiService divisasApiService) {
        this.divisasApiService = divisasApiService;
    }

    public FrankFurterResponse getLatestRates(String baseCurrency, String targetCurrencies, int amount) throws IOException {
        Call<FrankFurterResponse> call = divisasApiService.getLatestRates(baseCurrency, targetCurrencies);
        Response<FrankFurterResponse> response = call.execute(); // Llamada sincrónica


        if (response.isSuccessful()) {
            var exchangeRates = response.body().getExchangeRates();
            var convertedAmounts = new HashMap<String, Double>();

            exchangeRates.forEach((currency, rate) -> {
                double amountDouble = rate * amount;
                convertedAmounts.put(currency, amountDouble);
            });

            response.body().setExchangeRates(convertedAmounts);

            return response.body();
        } else {
            String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
            throw new IOException("Error fetching latest rates: " + errorMessage);
        }
    }
}
