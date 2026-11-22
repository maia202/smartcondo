package br.com.smartcondo.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartCondoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartCondo API")
                        .version("v1"));
    }
}
