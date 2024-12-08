package org.example.vivesbankproject.cuenta.dto.cuenta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa la información de una cuenta en el archivo ZIP")
public class CuentaZip {

    @Schema(description = "Identificador único de la cuenta en la base de datos", example = "1")
    private Long id;

    @Schema(description = "Identificador único global de la cuenta", example = "123e4567-e89b-12d3-a456-426614174001")
    private String guid;

    @Schema(description = "Código IBAN de la cuenta bancaria", example = "ES9121000418450200051332")
    private String iban;

    @Schema(description = "Saldo actual de la cuenta", example = "1500.75")
    private BigDecimal saldo;

    @Schema(description = "Tipo de cuenta asociado", implementation = TipoCuenta.class)
    private TipoCuenta tipoCuenta;

    @Schema(description = "Tarjeta vinculada a la cuenta", implementation = Tarjeta.class)
    private Tarjeta tarjeta;

    @Schema(description = "Identificador del cliente propietario de la cuenta", example = "123e4567-e89b-12d3-a456-426614174000")
    private String clienteId;

    @Schema(description = "Fecha de creación del registro", example = "2023-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2023-02-01T12:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Indica si la cuenta ha sido eliminada", example = "false")
    private Boolean isDeleted;
}
