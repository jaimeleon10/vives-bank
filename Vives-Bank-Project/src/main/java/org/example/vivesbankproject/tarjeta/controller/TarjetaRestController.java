package org.example.vivesbankproject.tarjeta.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tipo;
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
            @RequestParam Optional<Integer> cvv,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> caducidad,
            @RequestParam Optional<TipoTarjeta> tipoTarjeta,
            @RequestParam Optional<Double> limiteDiario,
            @RequestParam Optional<Double> limiteSemanal,
            @RequestParam Optional<Double> limiteMensual,
            @RequestParam Optional<UUID> cuentaId,
            Pageable pageable) {

        log.info("Recibiendo solicitud para listar tarjetas con filtros proporcionados");

        Page<TarjetaResponse> tarjetas = tarjetaService.getAll(
                numero, cvv, caducidad, tipoTarjeta, limiteDiario,
                limiteSemanal, limiteMensual, cuentaId, pageable);

        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaResponse> getTarjetaById(@PathVariable UUID id) {
        return tarjetaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TarjetaResponse> createTarjeta(@RequestBody TarjetaRequest tarjetaRequest) {
        TarjetaResponse createdTarjeta = tarjetaService.save(tarjetaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTarjeta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarjetaResponse> updateTarjeta(
            @PathVariable UUID id,
            @RequestBody TarjetaRequest tarjetaRequest) {
        try {
            TarjetaResponse updatedTarjeta = tarjetaService.update(id, tarjetaRequest);
            return ResponseEntity.ok(updatedTarjeta);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TarjetaResponse> deleteTarjetaById(@PathVariable UUID id) {
        try {
            TarjetaResponse tarjetaEliminada = tarjetaService.deleteById(id);
            return ResponseEntity.ok(tarjetaEliminada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tipo/{nombre}")
    public ResponseEntity<TipoTarjeta> getTipoTarjetaByNombre(@PathVariable Tipo nombre) {
        try {
            TipoTarjeta tipoTarjeta = tarjetaService.getTipoTarjetaByNombre(nombre);
            return ResponseEntity.ok(tipoTarjeta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}