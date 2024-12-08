package org.example.vivesbankproject.utils.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Configuración de seguridad para la aplicación.
 *
 * <p>Esta clase configura la seguridad de la aplicación utilizando Spring Security,
 * incluyendo el manejo de usuarios, filtros de autenticación JWT y control de acceso a recursos.</p>
 *
 * <p>Se integra con el sistema de autenticación y autoriza el acceso a las rutas dependiendo
 * de las reglas definidas.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final UserDetailsService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${api.version}")
    private String apiVersion;

    /**
     * Constructor para inyectar dependencias necesarias.
     *
     * @param userService Servicio de gestión de usuarios.
     * @param jwtAuthenticationFilter Filtro de autenticación JWT.
     */
    @Autowired
    public SecurityConfig(UserDetailsService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad para manejar las solicitudes HTTP.
     *
     * @param http Configurador de seguridad HTTP.
     * @return Cadena de filtros configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Podemos decir que forzamos el uso de HTTPS, para algunas rutas de la API o todas
                // Requerimos HTTPS para todas las peticiones, pero ojo que devuelve 302 para los test
                // .requiresChannel(channel -> channel.anyRequest().requiresSecure())

                // Deshabilitamos CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Sesiones
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                // Lo primero es decir a qué URLs queremos dar acceso libre
                // Lista balanca de comprobación

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        // Abrimos a Swagger -- Quitar en producción
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Permitimos el acceso a los recursos estáticos
                        .requestMatchers("/static/**").permitAll()
                        // Websockets para notificaciones
                        .requestMatchers("/ws/**").permitAll()
                        // Otras rutas de la API podemos permitiras o no....
                        .requestMatchers("/" + apiVersion + "/**").permitAll()
                        .anyRequest().authenticated())

                // Añadimos el filtro de autenticación
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devolvemos la configuración
        return http.build();
    }

    /**
     * Configura el codificador de contraseñas utilizando BCrypt.
     *
     * @return Instancia de PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación que utiliza los detalles del usuario y el codificador de contraseñas.
     *
     * @return Instancia de AuthenticationProvider configurada.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Administrador de autenticación utilizado en la configuración de seguridad.
     *
     * @param config Configuración de autenticación.
     * @return Instancia de AuthenticationManager configurada.
     * @throws Exception Si ocurre un error al obtener el administrador.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
