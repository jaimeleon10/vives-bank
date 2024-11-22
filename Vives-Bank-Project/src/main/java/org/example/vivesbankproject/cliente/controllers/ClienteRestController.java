
package org.example.vivesbankproject.cliente.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.service.ClienteService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("${api.version}/cliente")

public class ClienteRestController {
    private final ClienteService clienteService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public ClienteRestController(ClienteService clienteService, PaginationLinksUtils paginationLinksUtils) {
        this.clienteService = clienteService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ClienteResponse>> getAll(

            @RequestParam(required = false) Optional<String> dni,
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> apellido,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<String> telefono,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("getAll:  dni: {},nombre: {},apellido: {}, email: {}, telefono: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                 dni,nombre,apellido, email,telefono, page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<ClienteResponse> pageResult = clienteService.getAll(dni,nombre, apellido, email,telefono, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<ClienteResponse> getById(@PathVariable String id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(clienteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> createCliente(@Valid @RequestBody ClienteRequestSave clienteRequestSave) {
        log.info("save: clienteRequest: {}", clienteRequestSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(clienteRequestSave));
    }

    @PutMapping("{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@PathVariable String id, @Valid @RequestBody ClienteRequestUpdate clienteRequest) {
        log.info("update: id: {}, clienteRequest: {}", id, clienteRequest);
        return ResponseEntity.ok(clienteService.update(id, clienteRequest));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable String id) {
        log.info("delete: id: {}", id);
        clienteService.deleteById(id);
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
