package org.example.vivesbankproject.cliente.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "CLIENTES")
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^\\d{8}[A-Za-z]$", message = "El DNI debe tener 8 números seguidos de una letra")
    private String dni;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "Los apellidos no pueden estar vacío")
    private String apellidos;

    @Column(unique = true, nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener 9 números")
    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @Column(name = "FOTO_PERFIL")
    private String fotoPerfil;

    @Column(name = "FOTO_DNI", nullable = false)
    private String fotoDni;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cuentas")
    private List<Cuenta> cuentas;

    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    private ObjectId idMovimientos;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
