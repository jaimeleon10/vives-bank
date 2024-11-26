package org.example.vivesbankproject.movimientos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.example.vivesbankproject.movimientos.services.MovimientosService;
import org.example.vivesbankproject.movimientos.services.MovimientosServiceImpl;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<PageResponse<Movimientos>> getMovimientos(
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


    @GetMapping("/id/{id}")
    public ResponseEntity<Movimientos> getMovimientoById(@PathVariable String id) {
        log.info("Obteniendo movimiento con id: " + id);
        Movimientos movimiento = service.getById(id);
        return ResponseEntity.ok(movimiento);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Movimientos> getMovimientoByClienteId(@PathVariable String clienteId) {
        log.info("Obteniendo movimiento con id de cliente: " + clienteId);
        Movimientos movimiento = service.getByClienteId(clienteId);
        return ResponseEntity.ok(movimiento);
    }


    @PostMapping
    public ResponseEntity<Movimientos> createOrUpdateMovimientos(@RequestBody Movimientos movimiento) {
        log.info("Creando/actualizando movimiento: " + movimiento);
        Movimientos savedMovimiento = service.save(movimiento);
        return ResponseEntity.ok(savedMovimiento);
    }



}
