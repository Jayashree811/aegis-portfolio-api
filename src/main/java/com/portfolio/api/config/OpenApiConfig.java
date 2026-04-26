package com.portfolio.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI portfolioOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Portfolio Management API")
                        .description("API for managing portfolios, holdings, transactions, and dividends.")
                        .version("v1.0"));
    }
}
