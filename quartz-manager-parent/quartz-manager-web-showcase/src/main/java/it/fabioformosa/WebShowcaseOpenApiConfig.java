package it.fabioformosa;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebShowcaseOpenApiConfig {

  @Bean
  public OpenAPI webshowcaseOpenAPI() {
    OpenAPI openAPI = new OpenAPI()
      .info(new Info()
        .title("QUARTZ MANAGER DEMO API")
        .description("Quartz Manager- DEMO - REST API")
        .version("1.0.0")
        .license(new License()
          .name("Apache License 2.0")
          .url("https://github.com/fabioformosa/quartz-manager/blob/master/LICENSE")));
    return openAPI;
  }

  @Bean
  public GroupedOpenApi demoOpenApi() {
    return GroupedOpenApi.builder().group("demo").packagesToScan("it.fabioformosa.quartzmanager.controllers").build();
  }

}
