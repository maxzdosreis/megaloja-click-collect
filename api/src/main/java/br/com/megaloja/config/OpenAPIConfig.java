package br.com.megaloja.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agendador — Click & Collect API")
                        .description("API do sistema de agendamento e retirada de pedidos (Click & Collect). " +
                                "Gerencia produtos, lojas, estoque, pedidos, notificações e autorizações de retirada.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Megaloja")
                                .email("dev@megaloja.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Endpoints de autenticação e geração de tokens JWT"),
                        new Tag().name("Usuários").description("Gerenciamento de usuários do sistema"),
                        new Tag().name("Produtos").description("Gerenciamento de produtos do catálogo"),
                        new Tag().name("Lojas").description("Gerenciamento de lojas físicas"),
                        new Tag().name("Inventário").description("Consulta e atualização do estoque por loja e produto"),
                        new Tag().name("Pedidos").description("Gerenciamento de pedidos e agendamentos de retirada"),
                        new Tag().name("Notificações").description("Notificações relacionadas aos pedidos"),
                        new Tag().name("Autorizações de Retirada")
                                .description("Autorizações para terceiros retirarem pedidos")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .name("bearer-jwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT obtido no endpoint /auth/login")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
