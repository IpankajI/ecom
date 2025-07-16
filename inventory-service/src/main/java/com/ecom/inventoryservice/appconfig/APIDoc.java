package com.ecom.inventoryservice.appconfig;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Component
public class APIDoc {
    @Bean
    public OpenAPI openAPI(){
        Info info=new Info()
            .description("beta")
            .title("ecom apis")
            .version("v1");
        OpenAPI openAPI=new OpenAPI();
        openAPI.info(info);
        Server server=new Server();
        server.setUrl("localhost:8080");
        openAPI.setServers(Arrays.asList(server));
        return openAPI;
    }

}
