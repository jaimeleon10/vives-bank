package org.example.vivesbankproject.cuenta.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.version}/cuentas")
@Slf4j
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class CuentaController {
    private final CuentaService cuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CuentaController(CuentaService cuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.cuentaService = cuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<CuentaResponse>> getAll(
            @RequestParam(required = false) Optional<String> iban,
            @RequestParam(required = false) Optional<BigDecimal> saldoMax,
            @RequestParam(required = false) Optional<BigDecimal> saldoMin,
            @RequestParam(required = false) Optional<String> tipoCuenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Buscando todas las cuentas con las siguientes opciones: {}, {}, {}, {}", iban, saldoMax, saldoMin, tipoCuenta);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<CuentaResponse> pageResult = cuentaService.getAll(iban, saldoMax, saldoMin, tipoCuenta, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("cliente/{clienteGuid}")
    public ResponseEntity<ArrayList<CuentaResponse>> getAllCuentasByClienteGuid(@PathVariable String clienteGuid) {
        return ResponseEntity.ok(cuentaService.getAllCuentasByClienteGuid(clienteGuid));
    }

    @GetMapping("{id}")
    public ResponseEntity<CuentaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(cuentaService.getById(id));
    }

    @GetMapping("/iban/{iban}")
    public ResponseEntity<CuentaResponse> getByIban(@PathVariable String iban) {
        return ResponseEntity.ok(cuentaService.getByIban(iban));
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> save(@Valid @RequestBody CuentaRequest cuentaRequest) {
        var result = cuentaService.save(cuentaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<CuentaResponse> update(@PathVariable String id, @Valid @RequestBody CuentaRequestUpdate cuentaRequestUpdate) {
        var result = cuentaService.update(id, cuentaRequestUpdate);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Cuenta> delete(@PathVariable String id) {
        cuentaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Map<String, String> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            methodArgumentNotValidException.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            constraintViolationException.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        return errors;
    }
}