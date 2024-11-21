package org.example.vivesbankproject.tarjeta.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("${api.version}/tarjetas")
public class TarjetaRestController {

    private final TarjetaService tarjetaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public TarjetaRestController(TarjetaService tarjetaService, PaginationLinksUtils paginationLinksUtils) {
        this.tarjetaService = tarjetaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    public ResponseEntity<Page<TarjetaResponse>> getAll(
            @RequestParam Optional<String> numero,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> caducidad,
            @RequestParam Optional<TipoTarjeta> tipoTarjeta,
            @RequestParam Optional<Double> limiteDiario,
            @RequestParam Optional<Double> limiteSemanal,
            @RequestParam Optional<Double> limiteMensual,
            Pageable pageable) {

        log.info("Recibiendo solicitud para listar tarjetas con filtros proporcionados");

        Page<TarjetaResponse> tarjetas = tarjetaService.getAll(
                numero, caducidad, tipoTarjeta, limiteDiario,
                limiteSemanal, limiteMensual, pageable);

        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaResponse> getTarjetaById(@PathVariable String id) {
        return ResponseEntity.ok(tarjetaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TarjetaResponse> createTarjeta(@RequestBody TarjetaRequest tarjetaRequest) {
        TarjetaResponse createdTarjeta = tarjetaService.save(tarjetaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTarjeta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarjetaResponse> updateTarjeta(@PathVariable String id, @RequestBody TarjetaRequest tarjetaRequest) {
        try {
            TarjetaResponse updatedTarjeta = tarjetaService.update(id, tarjetaRequest);
            return ResponseEntity.ok(updatedTarjeta);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TarjetaResponse> deleteTarjetaById(@PathVariable String id) {
        try {
            TarjetaResponse tarjetaEliminada = tarjetaService.deleteById(id);
            return ResponseEntity.ok(tarjetaEliminada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}