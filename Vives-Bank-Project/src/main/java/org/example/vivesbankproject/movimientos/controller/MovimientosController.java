package org.example.vivesbankproject.movimientos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("${api.version}/movimientos")
@Slf4j
public class MovimientosController {

    private final MovimientosService service;

    private PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public MovimientosController(MovimientosServiceImpl service, PaginationLinksUtils paginationLinksUtils) {
        this.paginationLinksUtils = paginationLinksUtils;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Movimientos>> getMovimientos(
            @RequestParam(required = false) Optional<Cliente> cliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Obteniendo movimientos con las siquientes condiciones: " +  cliente);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        var movimientos = service.getAll(cliente, pageable);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(movimientos, uriBuilder))
                .body(PageResponse.of(movimientos, sortBy, direction));
    }


    @PostMapping
    public ResponseEntity<Transacciones> createOrUpdateMovimientos(@RequestBody Movimientos movimiento) {
        Transacciones savedTransaccion = service.save(movimiento);
        return ResponseEntity.ok(savedTransaccion);
    }



}
