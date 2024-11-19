package org.example.vivesbankproject.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "El DNI no puede ser un campo vacío")
    private String dni;

    @NotBlank(message = "El nombre no puede ser un campo vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden ser un campo vacío")
    private String apellidos;

    @Email(regexp = ".*@.*\\..*", message = "El email debe ser un campo válido")
    @NotBlank(message = "El email no puede ser un campo vacío")
    private String email;

    @NotBlank(message = "El teléfono no puede ser un campo vacío")
    private String telefono;

    private String fotoPerfil;
    private String fotoDni;
}
