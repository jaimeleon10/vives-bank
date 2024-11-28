package org.example.vivesbankproject.movimientos.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.movimientos.models.Transferencia;
import org.example.vivesbankproject.movimientos.services.MovimientosService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/me")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class MovimientosMeController {

    private final MovimientosService movimientosService;

    @Autowired
    public MovimientosMeController(MovimientosService movimientosService) {
        this.movimientosService = movimientosService;
    }

    @PostMapping("/domiciliacion/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoDomiciliacion(@AuthenticationPrincipal User user, @RequestBody @Valid Domiciliacion request) {

        return null;
    }

    @PostMapping("/ingresonomina/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoIngresoNomina(@AuthenticationPrincipal User user, @RequestBody @Valid IngresoDeNomina request) {

        return null;
    }

    @PostMapping("/pagotarjeta/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoPagoConTarjeta(@AuthenticationPrincipal User user, @RequestBody @Valid PagoConTarjeta request) {

        return null;
    }

    @PostMapping("/transferencia/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoTransferencia(@AuthenticationPrincipal User user, @PathVariable String iban , @RequestBody @Valid Transferencia request) {

        return null;
    }

    @DeleteMapping("/transferencia/{guid}")
    public ResponseEntity<MovimientoResponse> revocarMovimientoTransferencia(@AuthenticationPrincipal User user, @PathVariable String guid) {

        return null;
    }
}
