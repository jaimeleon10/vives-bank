package org.example.vivesbankproject.cliente.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Cache;


import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase que representa la entidad Cliente en la base de datos.
 * Contiene información personal, de contacto, dirección, cuentas, identificadores y el estado de borrado lógico.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"cuentas"})
public class Cliente {
    /** Identificador único de la base de datos */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de cliente en la base de datos", example = "1")
    private Long id;

    /** Identificador global único generado automáticamente */
    @Builder.Default
    @Schema(description = "GUID generado automáticamente para identificar el cliente de manera única", example = "a1b2c3d4")
    private String guid = IdGenerator.generarId();

    /** Número de identificación (DNI) único para el cliente */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "El DNI debe tener 8 números seguidos de una letra en mayúsculas")
    @Schema(description = "DNI del cliente. Debe contener 8 números seguidos de una letra en mayúsculas", example = "12345678T")
    private String dni;

    /** Nombre del cliente */
    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    /** Apellidos del cliente */
    @Column(nullable = false)
    @NotBlank(message = "Los apellidos no pueden estar vacío")
    @Schema(description = "Apellidos del cliente", example = "Pérez")
    private String apellidos;

    /** Dirección del cliente */
    @Embedded
    @NotNull(message = "La dirección no puede ser nula")
    @Schema(description = "Dirección completa del cliente")
    private Direccion direccion;

    /** Correo electrónico del cliente */
    @Column(unique = true, nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    @Schema(description = "Correo electrónico del cliente", example = "cliente@correo.com")
    private String email;

    /** Teléfono del cliente */
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener 9 números")
    @NotBlank(message = "El teléfono no puede estar vacío")
    @Schema(description = "Teléfono del cliente. Debe contener 9 números", example = "612345678")
    private String telefono;

    /** Foto de perfil del cliente */
    @NotBlank(message = "La foto de perfil no puede estar vacía")
    @Schema(description = "URL o ruta de la foto de perfil del cliente", example = "https://example.com/fotoPerfil.jpg")
    private String fotoPerfil;

    /** Foto del DNI del cliente */
    @Column(nullable = false)
    @NotBlank(message = "La foto del DNI no puede estar vacía")
    @Schema(description = "URL o ruta de la foto del DNI del cliente", example = "https://example.com/fotoDni.jpg")
    private String fotoDni;

    /** Conjunto de cuentas asociadas al cliente */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Conjunto de cuentas bancarias asociadas al cliente")
    private Set<Cuenta> cuentas = new HashSet<>();

    /** Relación con el usuario asignado al cliente */
    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "El usuario no puede ser un campo nulo")
    @Schema(description = "Usuario asociado al cliente", example = "user123")
    private User user;

    /** Fecha de creación del cliente */
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de creación del cliente en el sistema", example = "2023-12-07T10:15:30")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Fecha de la última actualización de datos del cliente */
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha de última actualización de datos del cliente en el sistema", example = "2023-12-07T11:30:00")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** Indicador lógico para determinar si el cliente está marcado como eliminado */
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Indicador lógico para determinar si el cliente está marcado como eliminado", example = "false")
    private Boolean isDeleted = false;
}
