package org.example.vivesbankproject.cuenta.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${api.version}/cuentas")
@Slf4j
public class CuentaController {
    private final CuentaService cuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CuentaController(CuentaService cuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.cuentaService = cuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Cuenta>> getAllPageable(
            @RequestParam(required = false) Optional<String> iban,
            @RequestParam(required = false) Optional<Double> saldo,
            @RequestParam(required = false) Optional<Cliente> cliente,
            @RequestParam(required = false) Optional<TipoCuenta> tipoCuenta,
            @RequestParam(required = false) Optional<Tarjeta> tarjeta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Buscando todas las cuentas con las siguientes opciones: {}, {}, {}, {}, {}", iban, saldo, cliente, tarjeta, tipoCuenta);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Cuenta> pageResult = cuentaService.getAll(iban, saldo, cliente, tarjeta, tipoCuenta, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<Optional<Cuenta>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(cuentaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Cuenta> save(@Valid @RequestBody Cuenta cuenta) {
        var result = cuentaService.save(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Cuenta> update(@PathVariable UUID id, @Valid @RequestBody Cuenta cuenta) {
        var result = cuentaService.update(id, cuenta);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Cuenta> delete(@PathVariable UUID id) {
        cuentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}