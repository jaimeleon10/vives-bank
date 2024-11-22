package org.example.vivesbankproject.tarjeta.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestSave;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequestUpdate;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<PageResponse<Tarjeta>> getAll(
            @RequestParam(required = false) Optional<String> numero,
            @RequestParam(required = false) Optional<LocalDate> caducidad,
            @RequestParam(required = false) Optional<TipoTarjeta> tipoTarjeta,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteDiario,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteDiario,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteSemanal,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteSemanal,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteMensual,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteMensual,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Recibiendo solicitud para listar tarjetas con filtros proporcionados");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Tarjeta> pageResult = tarjetaService.getAll(numero, caducidad, tipoTarjeta, minLimiteDiario, maxLimiteDiario, minLimiteSemanal, maxLimiteSemanal, minLimiteMensual, maxLimiteMensual, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<TarjetaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(tarjetaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TarjetaResponse> save( @RequestBody TarjetaRequestSave tarjetaRequestSave) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjetaService.save(tarjetaRequestSave));
    }

    @PutMapping("{id}")
    public ResponseEntity<TarjetaResponse> update(@PathVariable String id, @Valid @RequestBody TarjetaRequestUpdate tarjetaRequestUpdate) {
        return ResponseEntity.ok(tarjetaService.update(id, tarjetaRequestUpdate));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<TarjetaResponse> deleteTarjeta(@PathVariable String id) {
        log.info("Tarjeta borrada con id: {}", id);
        tarjetaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}