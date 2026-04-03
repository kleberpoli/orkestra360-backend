package com.project.orkestra360.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  /**
   * Configures the OpenAPI definition for the Orkestra 360 API. Provides basic metadata such as
   * title, version, and description for Swagger UI.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("Orkestra 360 API").version("1.0").description(""));
  }
}
