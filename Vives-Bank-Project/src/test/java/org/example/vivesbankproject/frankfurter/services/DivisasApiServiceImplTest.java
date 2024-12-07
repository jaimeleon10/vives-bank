package org.example.vivesbankproject.frankfurter.services;

import okhttp3.ResponseBody;
import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterConnectionException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterUnexpectedException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankfurterEmptyResponseException;
import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;

public class DivisasApiServiceImplTest {

    @Test
    public void testGetLatestRatesAsyncSuccess() throws ExecutionException, InterruptedException, IOException {
        String baseCurrency = "USD";
        String targetCurrencies = "EUR,GBP";
        String amount = "100.0";

        FrankFurterResponse mockResponse = new FrankFurterResponse();
        mockResponse.setExchangeRates(Map.of("EUR", 0.85, "GBP", 0.75));

        Retrofit mockRetrofit = mock(Retrofit.class);
        DivisasApiService mockApiClient = mock(DivisasApiService.class);
        Call<FrankFurterResponse> call = mock(Call.class);
        Response<FrankFurterResponse> response = Response.success(mockResponse);

        when(mockRetrofit.create(DivisasApiService.class)).thenReturn(mockApiClient);
        when(mockApiClient.getLatestRates(baseCurrency, targetCurrencies)).thenReturn(call);
        when(call.execute()).thenReturn(response);

        DivisasApiServiceImpl service = new DivisasApiServiceImpl(mockRetrofit);

        CompletableFuture<FrankFurterResponse> future = service.getLatestRatesAsync(baseCurrency, targetCurrencies, amount);
        FrankFurterResponse result = future.get();

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertTrue(result.getExchangeRates().containsKey("EUR"));
        assertTrue(result.getExchangeRates().containsKey("GBP"));
    }

    @Test
    public void testGetLatestRatesAsyncEmptyResponse() throws ExecutionException, InterruptedException, IOException {
        String baseCurrency = "USD";
        String targetCurrencies = "EUR,GBP";
        String amount = "100.0";

        // Simulamos una respuesta vacía
        Retrofit mockRetrofit = mock(Retrofit.class);
        DivisasApiService mockApiClient = mock(DivisasApiService.class);
        Call<FrankFurterResponse> call = mock(Call.class);
        Response<FrankFurterResponse> response = Response.success(null);  // Respuesta vacía

        when(mockRetrofit.create(DivisasApiService.class)).thenReturn(mockApiClient);
        when(mockApiClient.getLatestRates(baseCurrency, targetCurrencies)).thenReturn(call);
        when(call.execute()).thenReturn(response);

        DivisasApiServiceImpl service = new DivisasApiServiceImpl(mockRetrofit);

        // Ejecutar el método asíncrono y esperar que lance una excepción
        CompletableFuture<FrankFurterResponse> future = service.getLatestRatesAsync(baseCurrency, targetCurrencies, amount);

        // Verificar que la excepción se lance
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof FrankfurterEmptyResponseException);
    }

    @Test
    public void testGetLatestRatesAsyncConnectionError() throws ExecutionException, InterruptedException, IOException {
        String baseCurrency = "USD";
        String targetCurrencies = "EUR,GBP";
        String amount = "100.0";

        // Simulamos una excepción de conexión (IOException)
        Retrofit mockRetrofit = mock(Retrofit.class);
        DivisasApiService mockApiClient = mock(DivisasApiService.class);
        Call<FrankFurterResponse> call = mock(Call.class);

        when(mockRetrofit.create(DivisasApiService.class)).thenReturn(mockApiClient);
        when(mockApiClient.getLatestRates(baseCurrency, targetCurrencies)).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("Network error"));

        DivisasApiServiceImpl service = new DivisasApiServiceImpl(mockRetrofit);

        // Ejecutar el método asíncrono y esperar que lance una excepción
        CompletableFuture<FrankFurterResponse> future = service.getLatestRatesAsync(baseCurrency, targetCurrencies, amount);

        // Verificar que la excepción de conexión se lance
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof FrankFurterConnectionException);
    }

    @Test
    public void testGetLatestRatesAsyncUnexpectedError() throws ExecutionException, InterruptedException, IOException {
        String baseCurrency = "USD";
        String targetCurrencies = "EUR,GBP";
        String amount = "100.0";

        // Simulamos una respuesta no exitosa de la API (por ejemplo, 500 - Internal Server Error)
        Retrofit mockRetrofit = mock(Retrofit.class);
        DivisasApiService mockApiClient = mock(DivisasApiService.class);
        Call<FrankFurterResponse> call = mock(Call.class);

        // Creación de un ResponseBody vacío (no nulo)
        ResponseBody errorBody = ResponseBody.create(null, "");

        // Crear una respuesta de error con código 500
        Response<FrankFurterResponse> response = Response.error(500, errorBody);

        when(mockRetrofit.create(DivisasApiService.class)).thenReturn(mockApiClient);
        when(mockApiClient.getLatestRates(baseCurrency, targetCurrencies)).thenReturn(call);
        when(call.execute()).thenReturn(response);

        DivisasApiServiceImpl service = new DivisasApiServiceImpl(mockRetrofit);

        // Ejecutar el método asíncrono y esperar que lance una excepción
        CompletableFuture<FrankFurterResponse> future = service.getLatestRatesAsync(baseCurrency, targetCurrencies, amount);

        // Verificar que la excepción inesperada se lance
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof FrankFurterUnexpectedException);
    }
}