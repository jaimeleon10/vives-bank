package org.example.vivesbankproject.tarjeta.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("${api.version}/tarjetas")
public class TarjetaRestController {

    private final TarjetaService tarjetaService;
    private final PaginationLinksUtils paginationLinksUtils;
    private final TarjetaMapper tarjetaMapper;

    @Autowired
    public TarjetaRestController(TarjetaService tarjetaService, PaginationLinksUtils paginationLinksUtils, TarjetaMapper tarjetaMapper) {
        this.tarjetaService = tarjetaService;
        this.paginationLinksUtils = paginationLinksUtils;
        this.tarjetaMapper = tarjetaMapper;
    }

    @GetMapping
    public ResponseEntity<Page<Tarjeta>> getAll(@RequestParam Optional<String> numero, @RequestParam Optional<Integer> cvv,
            @RequestParam Optional<String> caducidad, @RequestParam Optional<TipoTarjeta> tipoTarjeta, @RequestParam Optional<Double> limiteDiario,
            @RequestParam Optional<Double> limiteSemanal, @RequestParam Optional<Double> limiteMensual, @RequestParam Optional<UUID> cuentaId,
            Pageable pageable) {

        log.info("Recibiendo solicitud para listar tarjetas con filtros proporcionados");

        Optional<LocalDate> caducidadParsed = caducidad.map(fecha -> LocalDate.parse(fecha, DateTimeFormatter.ISO_DATE));

        Page<Tarjeta> tarjetas = tarjetaService.getAll(
                numero, cvv, caducidadParsed, tipoTarjeta, limiteDiario, limiteSemanal, limiteMensual, cuentaId, pageable);

        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarjeta> getTarjetaById(@PathVariable UUID id) {
        Optional<Tarjeta> tarjeta = tarjetaService.getById(id);
        return tarjeta.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tarjeta> deleteTarjetaById(@PathVariable UUID id) {
        try {
            Tarjeta tarjetaEliminada = tarjetaService.deleteById(id);
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
