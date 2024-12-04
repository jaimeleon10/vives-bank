package org.example.vivesbankproject.frankfurter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class FrankFurterResponse {
    private String amount;
    private String base;
    private String date;

    @JsonProperty("rates")
    private Map<String, Double> exchangeRates;
}