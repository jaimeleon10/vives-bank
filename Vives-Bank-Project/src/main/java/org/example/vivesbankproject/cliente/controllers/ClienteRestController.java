
package org.example.vivesbankproject.cliente.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("${api.version}/clientes")
@Validated
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
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
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<ClienteResponse> pageResult = clienteService.getAll(dni,nombre, apellido, email,telefono, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("{id}")
    public ResponseEntity<ClienteResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteResponse> getByDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.getByDni(dni));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequestSave clienteRequestSave) {
        var result = clienteService.save(clienteRequestSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable String id, @Valid @RequestBody ClienteRequestUpdate clienteRequestUpdate) {
        var result = clienteService.update(id, clienteRequestUpdate);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clienteService.getUserAuthenticatedByGuid(user.getGuid()));
    }

    @PutMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteResponse> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody ClienteRequestUpdate clienteRequestUpdate) {
        var result = clienteService.updateUserAuthenticated(user.getGuid(), clienteRequestUpdate);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteMe(@AuthenticationPrincipal User user) {
        var result = clienteService.derechoAlOlvido(user.getGuid());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/me/dni_image")
    public ResponseEntity<ClienteResponse> updateDniImage(@AuthenticationPrincipal User user, MultipartFile file) {
        log.info("Actualizando imagen dni del cliente con guid: {}", user.getGuid());
        return ResponseEntity.ok(clienteService.updateDniFoto(user.getGuid(), file));
    }

    @PutMapping("/me/foto_perfil")
    public ResponseEntity<ClienteResponse> updateFotoPerfil(@AuthenticationPrincipal User user, MultipartFile file) {
        log.info("Actualizando imagen de perfil del cliente con guid: {}", user.getGuid());
        return ResponseEntity.ok(clienteService.updateProfileFoto(user.getGuid(), file));
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