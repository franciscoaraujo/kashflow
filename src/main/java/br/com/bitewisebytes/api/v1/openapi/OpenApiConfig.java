package br.com.bitewisebytes.api.v1.openapi;


import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kashflow API")
                        .description("API para gerenciamento de carteiras digitais, transações e auditorias")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipe Kashflow")
                                .email("contato@kashflow.com.br")
                                .url("https://www.kashflow.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                );
    }
}
