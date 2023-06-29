package ru.pricehunt.assortment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "PriceHunt Product Microservice API",
        description = "PriceHunt API", version = "1.0.0",
        contact = @Contact(
            name = "Vadim Burtelov",
            email = "v@burtelov.ru",
            url = "https://github.com/vburtelov"
        )
    )
)
public class OpenApiConfig {
}
