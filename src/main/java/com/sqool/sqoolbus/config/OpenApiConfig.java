package com.sqool.sqoolbus.config;

import com.sqool.sqoolbus.dto.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String tenantHeaderName = "X-Tenant-ID";
        final String apiTitle = "Sqoolbus School Transportation API";
        final String apiDescription = "A comprehensive REST API for managing school bus transportation system. Includes APIs for schools, pupils, routes, trips, and user management with role-based access control.";
        final String apiVersion = "1.0.0";

        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ))
                .info(new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .contact(new Contact()
                                .name("Sqoolbus Development Team")
                                .email("dev@sqoolbus.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(tenantHeaderName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme"))
                        .addSecuritySchemes(tenantHeaderName,
                                new SecurityScheme()
                                        .name(tenantHeaderName)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Tenant identifier for multi-tenant operations (e.g., default_sqool)")));
    }

    /**
     * Customizer to add both bearerAuth and X-Tenant-ID security requirements to all operations
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // Get the request path to determine if this is a master API or tenant API
            String path = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class) != null
                    ? handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class).value()[0]
                    : "";

            // Skip adding tenant header for master APIs and public endpoints
            if (path.startsWith("/api/master") || 
                path.startsWith("/swagger-ui") || 
                path.startsWith("/v3/api-docs") ||
                path.equals("/error")) {
                return operation;
            }

            // Clear existing security requirements to avoid duplicates
            if (operation.getSecurity() != null) {
                operation.getSecurity().clear();
            }

            // Add both security requirements to all tenant APIs
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            operation.addSecurityItem(new SecurityRequirement().addList("X-Tenant-ID"));

            return operation;
        };
    }
}