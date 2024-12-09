package org.example.vivesbankproject.rest.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponseCatalogo;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import java.util.List;

/**
 * Clase que representa la información de productos de cliente en el contexto de la aplicación.
 * Contiene listas de tipos de tarjetas y tipos de cuentas disponibles para un cliente.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa los productos financieros de un cliente")
public class ClienteProducto {

    /** Lista de tipos de tarjetas disponibles para el cliente */
    @Schema(description = "Lista de tipos de tarjetas disponibles", implementation = TipoTarjeta.class)
    private List<TipoTarjeta> tiposTarjetas;

    /** Lista de tipos de cuentas en el catálogo de cuentas financieras asociadas al cliente */
    @Schema(description = "Lista de tipos de cuentas en el catálogo de productos de cliente", implementation = TipoCuentaResponseCatalogo.class)
    private List<TipoCuentaResponseCatalogo> tiposCuentas;
}