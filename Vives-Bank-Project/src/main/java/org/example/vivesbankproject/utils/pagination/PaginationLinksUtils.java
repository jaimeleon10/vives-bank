package org.example.vivesbankproject.utils.pagination;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class para la creación de cabeceras de enlaces de paginación.
 * <p>
 * Proporciona funciones para construir enlaces de navegación en respuestas de paginación REST,
 * permitiendo al cliente navegar entre páginas utilizando los enlaces 'next', 'prev', 'first' y 'last'.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
@Tag(name = "PaginationLinksUtils", description = "Genera los encabezados de enlaces para la navegación por respuestas de paginación")
public class PaginationLinksUtils {

    /**
     * Crea la cabecera de enlaces para la navegación de paginación.
     *
     * <p>Genera enlaces para las acciones de paginación comunes como 'next', 'prev', 'first' y 'last'
     * dependiendo del estado actual de la navegación de las páginas.</p>
     *
     * @param page Página actual con información de paginación.
     * @param uriBuilder Herramienta para construir URIs con parámetros específicos.
     * @return Una cadena con los enlaces de navegación en el formato de cabecera HTTP.
     */
    @Operation(summary = "Genera los encabezados de enlace para la navegación de paginación")
    public String createLinkHeader(
            @Schema(description = "Página actual con información de paginación") Page<?> page,
            @Schema(description = "Herramienta para construir las URIs de navegación", implementation = UriComponentsBuilder.class)
            UriComponentsBuilder uriBuilder) {

        final StringBuilder linkHeader = new StringBuilder();

        if (page.hasNext()) {
            String uri = constructUri(page.getNumber() + 1, page.getSize(), uriBuilder);
            linkHeader.append(buildLinkHeader(uri, "next"));
        }

        if (page.hasPrevious()) {
            String uri = constructUri(page.getNumber() - 1, page.getSize(), uriBuilder);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(buildLinkHeader(uri, "prev"));
        }

        if (!page.isFirst()) {
            String uri = constructUri(0, page.getSize(), uriBuilder);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(buildLinkHeader(uri, "first"));
        }

        if (!page.isLast()) {
            String uri = constructUri(page.getTotalPages() - 1, page.getSize(), uriBuilder);
            appendCommaIfNecessary(linkHeader);
            linkHeader.append(buildLinkHeader(uri, "last"));
        }

        return linkHeader.toString();
    }

    /**
     * Construye una URI específica para el enlace de navegación.
     *
     * @param newPageNumber Número de la página objetivo.
     * @param size Tamaño de la página.
     * @param uriBuilder Herramienta para construir URIs.
     * @return URI construida con los parámetros de paginación.
     */
    private String constructUri(int newPageNumber, int size, UriComponentsBuilder uriBuilder) {
        return uriBuilder.replaceQueryParam("page", newPageNumber).replaceQueryParam("size", size).build().encode().toUriString();
    }

    /**
     * Crea el enlace individual en el formato requerido para las cabeceras HTTP.
     *
     * @param uri URI para el enlace de navegación.
     * @param rel Relación del enlace en la navegación de paginación.
     * @return Enlace formateado para el encabezado.
     */
    private String buildLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }

    /**
     * Añade una coma si el encabezado ya tiene algún enlace.
     *
     * <p>Esto permite concatenar múltiples enlaces en una sola cabecera de enlace HTTP.</p>
     *
     * @param linkHeader Cabecera de enlace actual.
     */
    private void appendCommaIfNecessary(final StringBuilder linkHeader) {
        if (!linkHeader.isEmpty()) {
            linkHeader.append(", ");
        }
    }
}