package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "domiciliaciones")
public class Domiciliacion {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();

    @Builder.Default
    private String guid = IdGenerator.generarId();

    @Pattern(
            regexp = "^ES\\d{20}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    private String ibanOrigen;

    @NotBlank
    private String ibanDestino;

    @Min(value = 1, message = "El importe no puede ser menor que 1")
    @Max(value = 10000, message = "El importe no puede ser mayor que 1000000000000")
    private BigDecimal cantidad;

    @Size(max = 100, message = "El nombre del acreedor no puede superar los 100 caracteres")
    private String nombreAcreedor;

    @NotNull
    @Builder.Default
    private LocalDateTime fechaInicio = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Periodicidad periodicidad = Periodicidad.MENSUAL;

    @Builder.Default
    private Boolean activa = true;

    @Builder.Default
    private LocalDateTime ultimaEjecucion = LocalDateTime.now(); // Última vez que se realizó el cargo
}
