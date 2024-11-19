package org.example.vivesbankproject.cliente.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Cliente extends User {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String dni;

    @NotBlank(message = "nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "apellidos no puede estar vacío")
    private String apellidos;

    @Column(unique = true, nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    @NotBlank(message = "Email no puede estar vacío")
    private String email;

    @Column(unique = true, nullable = false)
    private String telefono;

    @Column(name = "FOTO_PERFIL")
    private String fotoPerfil;

    @Column(name = "FOTO_DNI")
    private String fotoDni;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cuenta> cuentas;

    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
