package it.fabioformosa;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.Generated;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Generated
public class WebShowcaseOpenApiConfig {

  @Bean
  public OpenAPI webshowcaseOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("QUARTZ MANAGER DEMO API")
        .description("Quartz Manager- DEMO - REST API")
        .version("1.0.0")
        .license(new License()
          .name("Apache License 2.0")
          .url("https://github.com/fabioformosa/quartz-manager/blob/master/LICENSE")));
  }

  @Bean
  public GroupedOpenApi demoOpenApi() {
    return GroupedOpenApi.builder().group("demo").packagesToScan("it.fabioformosa.quartzmanager.controllers").build();
  }

}
