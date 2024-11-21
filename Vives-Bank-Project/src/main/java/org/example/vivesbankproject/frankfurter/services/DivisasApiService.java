package org.example.vivesbankproject.frankfurter.services;

import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Component
public interface DivisasApiService {
    @GET("/latest")
    Call<FrankFurterResponse> getLatestRates(@Query("base") String base, @Query("symbols") String symbols);
}
