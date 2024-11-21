package org.example.vivesbankproject.frankfurter.retrofit;

import okhttp3.OkHttpClient;
import org.example.vivesbankproject.frankfurter.services.DivisasApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class FrankFurterRetrofitConfig {

    @Value("${frankfurter.base-url:https://api.frankfurter.app}")
    private String baseUrl;

    @Bean
    public Retrofit retrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Bean
    public DivisasApiService divisasApiService(Retrofit retrofit) {
        return retrofit.create(DivisasApiService.class);
    }
}