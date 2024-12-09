package org.example.vivesbankproject.rest.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.hibernate.annotations.Cache;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase que representa la entidad de usuario en la base de datos.
 * Implementa la interfaz {@link UserDetails} para integrarse con Spring Security.
 * Contiene atributos relevantes para el sistema, roles, fecha de creación, fecha de actualización y otros campos de control.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "usuarios")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Entidad que representa a un usuario en el sistema.")
public class User implements Serializable, UserDetails {

    /** Identificador único para el usuario en la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único generado automáticamente para el usuario en la base de datos.")
    private Long id;

    /** GUID generado automáticamente para identificar de manera única a cada usuario. */
    @Builder.Default
    private String guid = IdGenerator.generarId();

    /** Nombre de usuario único para el acceso al sistema. */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username no puede estar vacío")
    @Schema(description = "Nombre de usuario único utilizado para el inicio de sesión.")
    private String username;

    /** Contraseña del usuario con requisitos de longitud mínima. */
    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Column(nullable = false)
    @Schema(description = "Contraseña del usuario, que debe tener al menos 5 caracteres.")
    private String password;

    /** Roles asignados al usuario, cargados de manera anticipada para su uso en la autorización. */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Roles asignados al usuario para definir sus permisos en el sistema.")
    private Set<Role> roles = Set.of(Role.USER);

    /** Fecha y hora de creación de la cuenta de usuario. */
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha y hora en la que se creó el usuario en el sistema.")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Fecha y hora de la última actualización en los datos del usuario. */
    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    @Schema(description = "Fecha y hora en la que se realizó la última modificación en la cuenta de usuario.")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** Indica si el usuario está marcado como eliminado (soft delete). */
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Indica si el usuario está marcado como eliminado en el sistema.")
    private Boolean isDeleted = false;
    /**
     * Obtiene las autorizaciones del usuario basándose en sus roles asignados.
     * Se convierte en un conjunto de `GrantedAuthority` para el sistema de Spring Security.
     *
     * @return Colección de las autorizaciones del usuario.
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }
}
