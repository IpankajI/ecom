package com.ecom.apigateway.appconfig;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Component
public class APIDoc {
    @Bean
    public OpenAPI openAPI(){
        Info info=new Info().
                description("beta").
                title("ecom apis").
                version("v1");
        OpenAPI openAPI=new OpenAPI();
        openAPI.info(info);
        Server server=new Server();
        server.setUrl("http://localhost:8080");
        openAPI.setServers(Arrays.asList(server));
        Components components=new Components();
        components.addSecuritySchemes("bearerAuth", createBearerTokenScheme());
        openAPI.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        openAPI.components(components);

        return openAPI;
    }

    private SecurityScheme createApiKeyScheme() {
        SecurityScheme securityScheme=new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("x-api-key")
                .scheme("basic")
                .in(SecurityScheme.In.HEADER);
        securityScheme.addExtension("x-api-name", "this is custome api name");    
        securityScheme.addExtension("x-api-v", "this is custome api version");    
        return securityScheme;
    }
    private SecurityScheme createApiSecretScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("x-api-secret")
                .scheme("basic")
                .in(SecurityScheme.In.HEADER);
    }
    private SecurityScheme createBearerTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .name("Authorization")
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER);
    }
}
