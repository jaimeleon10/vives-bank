package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCuentasRequest {
    @Builder.Default
    private Set<String> cuentasIds = new HashSet<>();
}
