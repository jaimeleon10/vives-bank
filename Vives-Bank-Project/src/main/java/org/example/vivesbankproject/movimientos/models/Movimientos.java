package org.example.vivesbankproject.movimientos.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.utils.IdGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document("movimientos")
@TypeAlias("Movimiento")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movimientos {
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();

    @Builder.Default
    private String guid = IdGenerator.generarId();

    @NotBlank(message = "El id del usuario no puede ser nulo")
    private String idUsuario;

    @NotBlank(message = "El cliente no puede estar vacio")
    private Cliente cliente;

    private List<Transacciones> transacciones;

    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }


    public void setTransacciones(List<Transacciones> transacciones) {
        this.transacciones = transacciones;
        if (transacciones != null) {
            this.totalItems = transacciones.size();
        } else {
            this.totalItems = 0;
        }
    }

    private Integer totalItems;

    public Integer getTotalItems() {
        return transacciones != null ? transacciones.size() : 0;
    }

    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default()
    private LocalDateTime updatedAt = LocalDateTime.now();
}
