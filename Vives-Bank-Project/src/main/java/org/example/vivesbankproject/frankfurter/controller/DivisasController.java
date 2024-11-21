//package org.example.vivesbankproject.frankfurter.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
//import org.example.vivesbankproject.frankfurter.services.DivisasApiServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("${api.version}/divisas")
//@Slf4j
//public class DivisasController {
//
//    private final DivisasApiServiceImpl divisasServiceImpl;
//
//    @Autowired
//    public DivisasController(DivisasApiServiceImpl divisasServiceImpl) {
//        this.divisasServiceImpl = divisasServiceImpl;
//    }
//
//    @GetMapping("/latest")
//    public ResponseEntity<FrankFurterResponse> getLatestRates(
//            @RequestParam(value = "amount" , defaultValue = "1") int amount,
//            @RequestParam(value = "base", defaultValue = "USD") String baseCurrency,
//            @RequestParam(value = "symbols", required = false) String symbol
//    ) throws IOException {
//        log.info("Obteniendo las últimas tasas de cambio desde {} a {}", baseCurrency, symbol);
//        FrankFurterResponse response = divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount);
//        return ResponseEntity.ok(response);
//    }
//}
//
//
//package org.example.vivesbankproject.frankfurter.controller;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.vivesbankproject.frankfurter.model.FrankFurterResponse;
//import org.example.vivesbankproject.frankfurter.services.DivisasApiServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//@RequestMapping("${api.version}/divisas")
//@Slf4j
//public class DivisasController {
//
//    private final DivisasApiServiceImpl divisasServiceImpl;
//
//    @Autowired
//    public DivisasController(DivisasApiServiceImpl divisasServiceImpl) {
//        this.divisasServiceImpl = divisasServiceImpl;
//    }
//
//    @GetMapping("/latest")
//    public CompletableFuture<ResponseEntity<FrankFurterResponse>> getLatestRates(
//            @RequestParam(value = "amount", defaultValue = "1") int amount,
//            @RequestParam(value = "base", defaultValue = "USD") String baseCurrency,
//            @RequestParam(value = "symbols", required = false) String symbol
//    ) {
//        log.info("Obteniendo las últimas tasas de cambio desde {} a {}", baseCurrency, symbol);
//        return divisasServiceImpl.getLatestRatesAsync(baseCurrency, symbol, amount)
//                .thenApply(ResponseEntity::ok)
//                .exceptionally(ex -> {
//                    log.error("Error al obtener las tasas de cambio", ex);
//                    return ResponseEntity.internalServerError().build();
//                });
//    }
//}