package org.example.vivesbankproject.utils.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.security.auth.services.jwt.JwtService;

import org.example.vivesbankproject.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación basado en JWT que se ejecuta una vez por solicitud.
 * Este filtro verifica la validez del token JWT y autentica al usuario si es válido.
 *
 * <p>Se utiliza junto con el servicio de gestión de JWT y un servicio de usuarios para
 * validar las credenciales de acceso.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService authUsersService;

    /**
     * Constructor para inicializar los servicios necesarios.
     *
     * @param jwtService Servicio para la gestión de tokens JWT.
     * @param authUsersService Servicio para la gestión de usuarios.
     */
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService authUsersService) {
        this.jwtService = jwtService;
        this.authUsersService = authUsersService;
    }

    /**
     * Método que intercepta cada solicitud para validar el token JWT.
     *
     * @param request La solicitud HTTP entrante.
     * @param response La respuesta HTTP saliente.
     * @param filterChain Cadena de filtros para procesar la solicitud.
     * @throws ServletException Si ocurre un error durante el filtrado.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Iniciando el filtro de autenticación");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        // Si no tenemos cabecera o no empieza por Bearer, no hacemos nada
        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info("No se ha encontrado cabecera de autenticación, se ignora");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        // Si tenemos cabecera, la extraemos y comprobamos que sea válida
        jwt = authHeader.substring(7);
        // Lo primero que debemos ver es que el token es válido
        try {
            userName = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            log.info("Token no válido");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido");
            return;
        }
        log.info("Usuario autenticado: {}", userName);
        if (StringUtils.hasText(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Comprobamos que el usuario existe y que el token es válido
            log.info("Comprobando usuario y token");
            try {
                userDetails = authUsersService.loadUserByUsername(userName);
            } catch (Exception e) {
                log.info("Usuario no encontrado: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }
            authUsersService.loadUserByUsername(userName);
            log.info("Usuario encontrado: {}", userDetails);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT válido");
                // Si es válido, lo autenticamos en el contexto de seguridad
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // Añadimos los detalles de la petición
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Lo añadimos al contexto de seguridad
                context.setAuthentication(authToken);
                // Y lo añadimos al contexto de seguridad
                SecurityContextHolder.setContext(context);
            }
        }
        // Y seguimos con la petición
        filterChain.doFilter(request, response);
    }
}
