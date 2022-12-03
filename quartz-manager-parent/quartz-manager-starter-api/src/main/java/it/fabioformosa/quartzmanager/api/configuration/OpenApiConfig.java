package it.fabioformosa.quartzmanager.api.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;


@Slf4j
@Configuration
@Generated
public class OpenApiConfig {

  @ConditionalOnProperty(name = "quartz-manager.oas.enabled")
  @ConditionalOnMissingBean
  @Bean
  public OpenAPI quartzManagerOpenAPI() {
    log.info("No OpenAPI found! Quart Manager is creating it...");
    return new OpenAPI().info(new Info()
      .title("QUARTZ MANAGER API")
      .description("Quartz Manager - REST API")
      .version("1.0.0")
      .license(new License()
        .name("Apache License 2.0")
        .url("https://github.com/fabioformosa/quartz-manager/blob/master/LICENSE")));
  }

  @ConditionalOnProperty(name = "quartz-manager.oas.enabled")
  @Bean
  public GroupedOpenApi quartzManagerStoreOpenApi(@Autowired(required = false) @Qualifier("quartzManagerOpenApiCustomiser") Optional<OpenApiCustomiser> openApiCustomiser) {
    String[] paths = {QuartzManagerPaths.QUARTZ_MANAGER_BASE_CONTEXT_PATH + "/**"};
    GroupedOpenApi.Builder groupedOpenApiBuilder = GroupedOpenApi.builder().group("quartz-manager").pathsToMatch(paths);
    openApiCustomiser.ifPresent(groupedOpenApiBuilder::addOpenApiCustomiser);
    return groupedOpenApiBuilder.build();
  }



}
