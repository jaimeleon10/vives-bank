package org.example.vivesbankproject.cliente.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.IdGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private static final Long DEFAULT_ID = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = DEFAULT_ID;

    @Builder.Default
    private String guid = IdGenerator.generarId();

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

    @NotBlank(message = "La foto de perfil no puede estar vacía")
    private String fotoPerfil;

    @Column(nullable = false)
    @NotBlank(message = "La foto del DNI no puede estar vacía")
    private String fotoDni;

    @OneToMany
    @JoinColumn(name = "cuentas_id")
    private Set<Cuenta> cuentas;

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "El usuario no puede ser un campo nulo")
    private User user;

    @Column(name = "id_movimientos")
    private ObjectId idMovimientos;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonProperty("idMovimientos")
    public String getIdMovimientos() {
        return idMovimientos != null ? idMovimientos.toHexString() : null;
    }
}
