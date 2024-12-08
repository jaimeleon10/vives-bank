package org.example.vivesbankproject.rest.movimientos.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("movimientos")
@TypeAlias("Movimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movimiento {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();

    @Builder.Default
    private String guid = IdGenerator.generarId();

    private String clienteGuid;

    private Domiciliacion domiciliacion;

    private IngresoDeNomina ingresoDeNomina;

    private PagoConTarjeta pagoConTarjeta;

    private Transferencia transferencia;

    @Builder.Default
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }

    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();

}

