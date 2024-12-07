package org.example.vivesbankproject.frankfurter.controller;

import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.example.vivesbankproject.frankfurter.services.DivisasApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", password = "password123")
class DivisasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DivisasApiServiceImpl divisasServiceImpl;

    @Test
    void testGetLatestRates() throws Exception {

        FrankFurterResponse mockResponse = new FrankFurterResponse();
        mockResponse.setAmount("30.0");
        mockResponse.setBase("EUR");
        mockResponse.setDate("2024-12-04");
        mockResponse.setExchangeRates(Map.of("USD", 31.476));

        when(divisasServiceImpl.getLatestRatesAsync(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mockResponse));

        mockMvc.perform(get("/v1/divisas/latest")
                        .param("amount", "30.0")
                        .param("base", "EUR")
                        .param("symbols", "USD"))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.amount").value("30.0"))
                //.andExpect(jsonPath("$.base").value("EUR"))
                //.andExpect(jsonPath("$.date").value("2024-12-04"))
                //.andExpect(jsonPath("$.exchangeRates.USD").value(31.476));
    }
}