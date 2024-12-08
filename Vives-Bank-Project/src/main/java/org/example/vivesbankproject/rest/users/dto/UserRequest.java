package org.example.vivesbankproject.rest.users.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.users.models.Role;
import org.hibernate.validator.constraints.Length;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username no puede estar vacio")
    private String username;

    @NotBlank(message = "Password no puede estar vacio")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    private Boolean isDeleted = false;
}