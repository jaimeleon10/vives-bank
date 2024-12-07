package org.example.vivesbankproject.frankfurter.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
import org.example.vivesbankproject.frankfurter.services.DivisasApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador encargado de manejar las operaciones relacionadas con divisas.
 * Permite obtener las últimas tasas de cambio de manera asíncrona utilizando el servicio correspondiente.
 * Protege el acceso solo a usuarios con el rol 'USER'.
 *  @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *  @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("${api.version}/divisas")
@Slf4j
@PreAuthorize("hasRole('USER')")
public class DivisasController {

    private final DivisasApiServiceImpl divisasServiceImpl;

    /**
     * Constructor que inyecta el servicio de divisas.
     *
     * @param divisasServiceImpl El servicio que contiene la lógica de negocio para la obtención de las tasas de cambio.
     */
    @Autowired
    public DivisasController(DivisasApiServiceImpl divisasServiceImpl) {
        this.divisasServiceImpl = divisasServiceImpl;
    }

    /**
     * Obtiene las últimas tasas de cambio de manera asíncrona.
     *
     * @param amount El monto base para la conversión. Por defecto es "1".
     * @param baseCurrency La divisa base para la conversión. Por defecto es "EUR".
     * @param symbol Opcional. Especifica las divisas objetivo para el tipo de cambio (separadas por comas).
     * @return Una respuesta asíncrona con las últimas tasas de cambio encapsuladas en un objeto {@link ResponseEntity}.
     */
    @GetMapping("/latest")

    public CompletableFuture<ResponseEntity<FrankFurterResponse>> getLatestRates(
            @Parameter(name = "amount", description = "Cantidad de la conversión (valor por defecto es 1)", example = "1")
            @RequestParam(value = "amount", defaultValue = "1") String amount,

            @Parameter(name = "base", description = "Moneda base para la conversión", example = "EUR")
            @RequestParam(value = "base", defaultValue = "EUR") String baseCurrency,

            @Parameter(name = "symbols", description = "Símbolos de divisas para convertir", example = "USD,GBP")
            @RequestParam(value = "symbols", required = false) String symbol
    ) {
        log.info("Obteniendo las últimas tasas de cambio desde {} a {}", baseCurrency, symbol);

        return divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount)
                .thenApply(result -> {
                    log.info("Respuesta construida: {}", result);
                    return ResponseEntity.ok(result);
                });
    }
}