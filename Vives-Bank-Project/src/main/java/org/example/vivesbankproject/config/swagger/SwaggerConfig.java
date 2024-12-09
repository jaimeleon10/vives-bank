package org.example.vivesbankproject.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    // Añadimos la configuración de JWT
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Vives Bank")
                                .version("1.0.0")
                                .description("API de un proyecto de un banco de 2º DAW. 2024")
                                .contact(
                                        new Contact()
                                                .name("Jaime León Mulero")
                                                .url("https://github.com/jaimeleon10")
                                )
                                .contact(
                                        new Contact()
                                                .name("Natalia González Álvarez")
                                                .url("https://github.com/ngalvez0910")
                                )
                                .contact(
                                        new Contact()
                                                .name("Alba García Orduña")
                                                .url("https://github.com/Alba448")
                                )
                                .contact(
                                        new Contact()
                                                .name("Álvaro Herrero")
                                                .url("https://github.com/alvarito304")
                                )
                                .contact(
                                        new Contact()
                                                .name("Germán Fernández Carrecedo")
                                                .url("https://github.com/germangfc")
                                )
                                .contact(
                                        new Contact()
                                                .name("Mario de Domingo Álvarez")
                                                .url("https://github.com/wolverine307mda")
                                )

                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Documentación del Proyecto")
                                .url("https://github.com/jaimeleon10/vives-bank/Documentacion")
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del Proyecto")
                                .url("https://github.com/jaimeleon10/vives-bank")
                )
                // Añadimos la seguridad JWT
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }


    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("https")
                // Algunas rutas son JWT
                .pathsToMatch("/v1/**") // Todas las rutas
                .displayName("API Vives Bank")
                .build();
    }
}