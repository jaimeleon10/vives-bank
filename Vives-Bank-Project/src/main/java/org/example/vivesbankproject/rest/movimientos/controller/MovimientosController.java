package org.example.vivesbankproject.rest.movimientos.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosService;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosServiceImpl;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.version}/movimientos")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class MovimientosController {

    private final MovimientosService service;

    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public MovimientosController(MovimientosServiceImpl service, PaginationLinksUtils paginationLinksUtils) {
        this.paginationLinksUtils = paginationLinksUtils;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovimientoResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Obteniendo todos los movimientos");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        var movimientos = service.getAll(pageable);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(movimientos, uriBuilder))
                .body(PageResponse.of(movimientos, sortBy, direction));
    }

    @GetMapping("/{guid}")
    public ResponseEntity<MovimientoResponse> getById(@PathVariable String guid) {
        log.info("Obteniendo movimiento con guid: {}", guid);
        MovimientoResponse movimiento = service.getByGuid(guid);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<MovimientoResponse> getByClienteGuid(@PathVariable String clienteId) {
        log.info("Obteniendo movimiento con id de cliente: {}", clienteId);
        MovimientoResponse movimiento = service.getByClienteGuid(clienteId);
        return ResponseEntity.ok(movimiento);
    }


    @PostMapping
    public ResponseEntity<MovimientoResponse> save(@RequestBody MovimientoRequest movimiento) {
        log.info("Creando/actualizando movimiento: " + movimiento);
        MovimientoResponse savedMovimiento = service.save(movimiento);
        return ResponseEntity.ok(savedMovimiento);
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
