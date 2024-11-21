package org.example.vivesbankproject.tarjeta.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.vivesbankproject.utils.IdGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "tipoTarjetas")
@NoArgsConstructor
@AllArgsConstructor
public class TipoTarjeta {

    private static final Long DEFAULT_ID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = DEFAULT_ID;

    @Builder.Default
    private String guid = IdGenerator.generarId();

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre del tipo de tarjeta no puede estar vacío")
    private Tipo nombre;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
