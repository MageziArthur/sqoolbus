package com.sqool.sqoolbus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
public class OpenApiConfig {

    private static final Pattern TENANT_SETUP_PATH_PATTERN = Pattern.compile("^/api/tenants/[^/]+/setup$");

    /**
     * Master API Group - for tenant management and master authentication
     */
    @Bean
    public GroupedOpenApi masterApi() {
        return GroupedOpenApi.builder()
                .group("master")
                .displayName("Master APIs")
            .pathsToMatch("/api/master/**", "/api/tenants", "/api/tenants/**", "/api/users/**", "/api/otp/**", "/api/trips/**")
                .addOperationCustomizer(masterOperationCustomizer())
                .build();
    }

    /**
     * Tenant API Group - for tenant-specific operations
     */
    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder()
                .group("tenant")
                .displayName("Tenant APIs")
                .pathsToMatch("/api/**")
            .pathsToExclude("/api/master/**", "/api/tenants", "/api/tenants/**", "/api/users/**", "/api/otp/**")
                .addOperationCustomizer(tenantOperationCustomizer())
                .build();
    }

    /**
     * Common OpenAPI configuration shared by both groups
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development server")
                ))
                .info(new Info()
                        .title("Sqoolbus School Transportation API")
                        .description("A comprehensive REST API for managing school bus transportation system")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sqoolbus Development Team")
                                .email("dev@sqoolbus.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme"))
                        .addSecuritySchemes("X-Tenant-ID",
                                new SecurityScheme()
                                        .name("X-Tenant-ID")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Tenant identifier for multi-tenant operations (e.g., default_sqool)")));
    }

    /**
     * Operation customizer for Master APIs - only requires Bearer token
     */
    private OperationCustomizer masterOperationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // Clear existing security requirements
            if (operation.getSecurity() != null) {
                operation.getSecurity().clear();
            }

            // Master APIs only need Bearer token (master token)
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            // Trip APIs still require tenant context even when shown in master Swagger group
            if (isTripsApi(handlerMethod)) {
                operation.addSecurityItem(new SecurityRequirement().addList("X-Tenant-ID"));
            }

            return operation;
        };
    }

    private boolean isTripsApi(HandlerMethod handlerMethod) {
        RequestMapping classMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        if (classMapping == null || classMapping.value().length == 0) {
            return false;
        }

        return Arrays.stream(classMapping.value())
                .anyMatch(path -> path != null && path.startsWith("/api/trips"));
    }

    /**
     * Operation customizer for Tenant APIs - requires both Bearer token and X-Tenant-ID
     */
    private OperationCustomizer tenantOperationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            // Clear existing security requirements
            if (operation.getSecurity() != null) {
                operation.getSecurity().clear();
            }

            // Tenant APIs need both Bearer token and X-Tenant-ID header
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            if (!isTenantSetupApi(handlerMethod)) {
                operation.addSecurityItem(new SecurityRequirement().addList("X-Tenant-ID"));
            }

            return operation;
        };
    }

    private boolean isTenantSetupApi(HandlerMethod handlerMethod) {
        RequestMapping classMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequestMapping.class);

        if (classMapping == null || classMapping.value().length == 0 || methodMapping == null || methodMapping.value().length == 0) {
            return false;
        }

        RequestMethod[] methods = methodMapping.method();
        if (methods.length > 0 && Arrays.stream(methods).noneMatch(RequestMethod.POST::equals)) {
            return false;
        }

        return Arrays.stream(classMapping.value())
                .flatMap(classPath -> Arrays.stream(methodMapping.value())
                        .map(methodPath -> classPath + methodPath))
                .anyMatch(path -> TENANT_SETUP_PATH_PATTERN.matcher(path).matches());
    }
}