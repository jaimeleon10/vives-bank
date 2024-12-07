package org.example.vivesbankproject.frankfurter.controller;

import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterConnectionException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankFurterUnexpectedException;
import org.example.vivesbankproject.frankfurter.exceptions.FrankfurterEmptyResponseException;
import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.example.vivesbankproject.frankfurter.services.DivisasApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", password = "userPass123")
class DivisasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DivisasApiServiceImpl divisasServiceImpl;

    @Test
    public void GetLatestRates() throws Exception {
        Double amount = 1.0;
        String baseCurrency = "EUR";
        String symbol = "USD";

        FrankFurterResponse response = new FrankFurterResponse();
        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("USD", 1.2);
        response.setExchangeRates(exchangeRates);
        CompletableFuture<FrankFurterResponse> future = CompletableFuture.completedFuture(response);

        when(divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount)).thenReturn(future);

        mockMvc.perform(get("/v1/divisas/latest")
                        .param("amount", amount.toString())
                        .param("base", baseCurrency)
                        .param("symbols", symbol))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void GetLatestRates_UnexpectedError() throws Exception {
        Double amount = 1.0;
        String baseCurrency = "EUR";
        String symbol = "USD";

        when(divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount))
                .thenThrow(new FrankFurterUnexpectedException(baseCurrency, symbol));

        mockMvc.perform(get("/v1/divisas/latest")
                        .param("amount", amount.toString())
                        .param("base", baseCurrency)
                        .param("symbols", symbol))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void GetLatestRates_EmptyResponse() throws Exception {

        Double amount = 1.0;
        String baseCurrency = "EUR";
        String symbol = "USD";

        when(divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount))
                .thenThrow(new FrankfurterEmptyResponseException(baseCurrency, symbol, amount));

        mockMvc.perform(get("/v1/divisas/latest")
                        .param("amount", amount.toString())
                        .param("base", baseCurrency)
                        .param("symbols", symbol))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void GetLatestRates_ConnectionError() throws Exception {
        Double amount = 1.0;
        String baseCurrency = "EUR";
        String symbol = "USD";

        when(divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount))
                .thenThrow(new FrankFurterConnectionException(baseCurrency, symbol, new IOException("Connection failed")));

        mockMvc.perform(get("/v1/divisas/latest")
                        .param("amount", amount.toString())
                        .param("base", baseCurrency)
                        .param("symbols", symbol))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}