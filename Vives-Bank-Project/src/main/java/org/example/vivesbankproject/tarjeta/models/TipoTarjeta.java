package org.example.vivesbankproject.tarjeta.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeración que define los tipos de tarjetas bancarias.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Schema(description = "Tipos de tarjetas bancarias")
public enum TipoTarjeta {
    /**
     * Tarjeta de crédito, que permite realizar compras con un límite de crédito.
     */
    @Schema(description = "Tarjeta de crédito")
    CREDITO,

    /**
     * Tarjeta de débito, que utiliza fondos directamente de la cuenta asociada.
     */
    @Schema(description = "Tarjeta de débito")
    DEBITO
}