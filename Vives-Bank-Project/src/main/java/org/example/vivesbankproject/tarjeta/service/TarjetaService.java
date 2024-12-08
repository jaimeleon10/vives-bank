package org.example.vivesbankproject.tarjeta.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponsePrivado;
import org.example.vivesbankproject.rest.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Tag(name = "Tarjeta Service", description = "API for managing bank cards")
public interface TarjetaService {

    @Operation(summary = "Get all bank cards with optional filtering",
            description = "Retrieve bank cards with optional filters and pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved bank cards",
                            content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid filtering parameters")
            })
    Page<TarjetaResponse> getAll(
            @Parameter(description = "Card number for filtering") Optional<String> numero,
            @Parameter(description = "Expiration date for filtering") Optional<LocalDate> caducidad,
            @Parameter(description = "Card type for filtering") Optional<TipoTarjeta> tipoTarjeta,
            @Parameter(description = "Minimum daily limit") Optional<BigDecimal> minLimiteDiario,
            @Parameter(description = "Maximum daily limit") Optional<BigDecimal> maxLimiteDiario,
            @Parameter(description = "Minimum weekly limit") Optional<BigDecimal> minLimiteSemanal,
            @Parameter(description = "Maximum weekly limit") Optional<BigDecimal> maxLimiteSemanal,
            @Parameter(description = "Minimum monthly limit") Optional<BigDecimal> minLimiteMensual,
            @Parameter(description = "Maximum monthly limit") Optional<BigDecimal> maxLimiteMensual,
            @Parameter(description = "Pagination information") Pageable pageable);

    @Operation(summary = "Get a bank card by its ID",
            description = "Retrieve a specific bank card using its unique identifier",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the bank card",
                            content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bank card not found")
            })
    TarjetaResponse getById(
            @Parameter(description = "Unique identifier of the bank card", required = true) String id);

    @Operation(summary = "Get a bank card by its card number",
            description = "Retrieve a specific bank card using its card number",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the bank card",
                            content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bank card not found")
            })
    TarjetaResponse getByNumeroTarjeta(
            @Parameter(description = "Card number to retrieve the bank card", required = true) String numeroTarjeta);

    @Operation(summary = "Get private data for a bank card",
            description = "Retrieve private information for a bank card after user authentication",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved private card data",
                            content = @Content(schema = @Schema(implementation = TarjetaResponsePrivado.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access"),
                    @ApiResponse(responseCode = "404", description = "Card or user not found")
            })
    TarjetaResponsePrivado getPrivateData(
            @Parameter(description = "Unique identifier of the bank card", required = true) String id,
            @Parameter(description = "Request containing user authentication details", required = true) TarjetaRequestPrivado tarjetaRequestPrivado);

    @Operation(summary = "Create a new bank card",
            description = "Save a new bank card in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created the bank card",
                            content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid card data")
            })
    TarjetaResponse save(
            @Parameter(description = "Bank card creation request", required = true) TarjetaRequestSave tarjetaRequestSave);

    @Operation(summary = "Update an existing bank card",
            description = "Update the details of an existing bank card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the bank card",
                            content = @Content(schema = @Schema(implementation = TarjetaResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Bank card not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid update data")
            })
    TarjetaResponse update(
            @Parameter(description = "Unique identifier of the bank card to update", required = true) String id,
            @Parameter(description = "Bank card update request", required = true) TarjetaRequestUpdate tarjetaRequestUpdate);

    @Operation(summary = "Delete a bank card",
            description = "Mark a bank card as deleted in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted the bank card"),
                    @ApiResponse(responseCode = "404", description = "Bank card not found")
            })
    void deleteById(
            @Parameter(description = "Unique identifier of the bank card to delete", required = true) String id);
}