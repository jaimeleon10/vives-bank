package org.example.vivesbankproject.movimientoTransaccion.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.example.vivesbankproject.movimientoTransaccion.services.MovimientoTransaccionService;
import org.example.vivesbankproject.utils.PageResponse;
import org.example.vivesbankproject.utils.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("${api.version}/movimientostransaccion")
@Slf4j
public class MovimientoTransaccionController {

    private final MovimientoTransaccionService service;

    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public MovimientoTransaccionController(MovimientoTransaccionService service, PaginationLinksUtils paginationLinksUtils) {
        this.paginationLinksUtils = paginationLinksUtils;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<MovimientoTransaccion>> getMovimientosTransaccion(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Obteniendo todos los movimientos transaccion");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        var movimientos = service.getAll(pageable);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(movimientos, uriBuilder))
                .body(PageResponse.of(movimientos, sortBy, direction));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MovimientoTransaccion> getMovimientoTransaccionById(@PathVariable String id) {
        log.info("Obteniendo movimiento transaccion con id: " + id);
        MovimientoTransaccion movimientoTransaccion = service.getById(id);
        return ResponseEntity.ok(movimientoTransaccion);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<MovimientoTransaccion> getMovimientoTransaccionByClienteId(@PathVariable String clienteId) {
        log.info("Obteniendo movimiento transaccion con id de cliente: " + clienteId);
        MovimientoTransaccion movimientoTransaccion = service.getByClienteId(clienteId);
        return ResponseEntity.ok(movimientoTransaccion);
    }


    @PostMapping
    public ResponseEntity<MovimientoTransaccion> createOrUpdateMovimientos(@RequestBody MovimientoTransaccion movimientoTransaccion) {
        log.info("Creando/actualizando movimiento transaccion: " + movimientoTransaccion);
        MovimientoTransaccion savedMovimientoTransaccion = service.save(movimientoTransaccion);
        return ResponseEntity.ok(savedMovimientoTransaccion);
    }
}