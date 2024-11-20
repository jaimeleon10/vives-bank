package org.example.vivesbankproject.movimientos.models;

import jakarta.validation.constraints.Pattern;

public class PagoConTarjeta extends Transacciones{
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;
}
