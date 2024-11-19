package org.example.vivesbankproject.cliente.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteInfoResponse;
import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("${api.version}/cliente")

public class ClienteRestController {
    private final ClienteService clienteService;
    private final CuentaService cuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public ClienteRestController(ClienteService clienteService, CuentaService cuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.clienteService = clienteService;
        this.cuentaService = cuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ClienteResponse>> findAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> dni,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<String> telefono,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("findAll: nombre: {}, dni: {}, email: {}, telefono: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                nombre, dni, email,telefono, page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<ClienteResponse> pageResult = clienteService.findAll(nombre, dni, email,telefono, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteInfoResponse> findById(@PathVariable UUID id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> createCliente(@Valid @RequestBody ClienteRequest clienteRequest) {
        log.info("save: clienteRequest: {}", clienteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@PathVariable UUID id, @Valid @RequestBody ClienteRequest clienteRequest) {
        log.info("update: id: {}, clienteRequest: {}", id, clienteRequest);
        return ResponseEntity.ok(clienteService.update(id, clienteRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable UUID id) {
        log.info("delete: id: {}", id);
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ClienteInfoResponse> me(@AuthenticationPrincipal Cliente cliente) {
        log.info("Obteniendo perfil del cliente");
        return ResponseEntity.ok(clienteService.findById(cliente.getId()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ClienteResponse> updateMe(@AuthenticationPrincipal Cliente cliente, @Valid @RequestBody ClienteRequest clienteRequest) {
        log.info("updateMe: cliente: {}, clienteRequest: {}", cliente, clienteRequest);
        return ResponseEntity.ok(clienteService.update(cliente.getId(), clienteRequest));
    }

    @DeleteMapping("/me/profile")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal Cliente cliente) {
        log.info("deleteMe: cliente: {}", cliente);
        clienteService.deleteById(cliente.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/cuentas")
    public ResponseEntity<PageResponse<Cuenta>> getCuentasByCliente(
            @AuthenticationPrincipal Cliente cliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo cuentas del cliente con id: {}", cliente.getId());
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(clienteService.getCuentasByClienteId(cliente.getId(), pageable), sortBy, direction));
    }

    @GetMapping("/me/cuentas")
    public ResponseEntity<PageResponse<Cuenta>> getCuentaByCliente(
            @AuthenticationPrincipal Cliente cliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo cuentas del cliente con id: {}", cliente.getId());
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(cuentaService.getCuentaByClienteId(cliente.getId(), pageable), sortBy, direction));
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