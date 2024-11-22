package org.example.vivesbankproject.cuenta.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.TipoCuentaService;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("${api.version}/tipocuentas")
@Slf4j
public class TipoCuentaController {
    private final TipoCuentaService tipoCuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public TipoCuentaController(TipoCuentaService tipoCuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.tipoCuentaService = tipoCuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<TipoCuentaResponse>> getAllPageable(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<BigDecimal> interes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Buscando todos los tipos de cuentas con las siguientes opciones: {}, {}", nombre, interes);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<TipoCuentaResponse> pageResult = tipoCuentaService.getAll(nombre, interes, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<TipoCuentaResponse> getById(@PathVariable String id) {
        log.info("Buscando el tipo de cuenta con id: {}", id);
        return ResponseEntity.ok(tipoCuentaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TipoCuentaResponse> save(@Valid @RequestBody TipoCuentaRequest tipoCuentaRequest) {
        log.info("Creando nuevo tipo de cuenta: {}", tipoCuentaRequest);
        var result = tipoCuentaService.save(tipoCuentaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<TipoCuentaResponse> update(@PathVariable String id, @Valid @RequestBody TipoCuentaRequest tipoCuentaRequest) {
        log.info("Actualizando tipo de cuenta con id: {}", id);
        var result = tipoCuentaService.update(id, tipoCuentaRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        log.info("Eliminando tipo de cuenta con id: {}", id);
        tipoCuentaService.deleteById(id);
    }
}