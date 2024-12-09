package org.example.vivesbankproject.frankfurter.retrofit;

import okhttp3.OkHttpClient;
import org.example.vivesbankproject.frankfurter.services.DivisasApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Configuración para la integración con el servicio Frankfurter utilizando Retrofit.
 * Define la configuración de tiempo de espera, URL base y la creación de servicios Retrofit necesarios
 * para interactuar con la API de tasas de cambio de Frankfurter.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
public class FrankFurterRetrofitConfig {

    /**
     * URL base de la API de Frankfurter. Se obtiene desde las propiedades de configuración o usa un valor por defecto.
     */
    @Value("${frankfurter.base-url:https://api.frankfurter.app}")
    private String baseUrl;

    /**
     * Configura la instancia de Retrofit para realizar llamadas a la API de Frankfurter con un cliente OkHttp optimizado.
     *
     * @return la instancia de Retrofit configurada.
     */
    @Bean
    public Retrofit retrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS) // Tiempo de espera de lectura
                .connectTimeout(30, TimeUnit.SECONDS) // Tiempo de espera de conexión
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create()) // Convierte respuestas JSON en objetos Java
                .build();
    }

    /**
     * Crea el servicio DivisasApiService utilizando la instancia Retrofit configurada previamente.
     *
     * @param retrofit la instancia Retrofit configurada.
     * @return la implementación de DivisasApiService para realizar llamadas a la API.
     */
    @Bean(name = "retrofitDivisasApiService")
    public DivisasApiService divisasApiService(Retrofit retrofit) {
        return retrofit.create(DivisasApiService.class);
    }
}