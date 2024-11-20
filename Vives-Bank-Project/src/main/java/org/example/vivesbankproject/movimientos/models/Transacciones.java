package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
@Builder
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Transferencia.class, name = "TRANSFERENCIA"),
        @JsonSubTypes.Type(value = PagoConTarjeta.class, name = "PAGO_CON_TARJETA"),
        @JsonSubTypes.Type(value = IngresoDeNomina.class, name = "INGRESO_DE_NOMINA"),
        @JsonSubTypes.Type(value = Domiciliacion.class, name = "DOMICILIACION")
})
public abstract class Transacciones {
    @Builder.Default
    private ObjectId id = new ObjectId();
    @Builder.Default
    private LocalDateTime fecha_transaccion = LocalDateTime.now();
    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private Double cantidad;
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "El campo solo puede contener letras y espacios")
    @Size(max = 100, message = "El campo no puede tener más de 100 caracteres")
    private String concepto;
}