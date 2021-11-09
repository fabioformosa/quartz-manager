package it.fabioformosa.quartzmanager.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(apiInfo());
  }

	private Info apiInfo() {
    return new Info()
      .title("QUARTZ MANAGER API")
      .description("Quartz Manager - REST API")
      .version("1.0.0")
      .license(new License()
        .name("Apache License 2.0")
        .url("https://github.com/fabioformosa/quartz-manager/blob/master/LICENSE"));
	}

//	private SecurityContext securityContext() {
//		return SecurityContext.builder().forPaths(PathSelectors.any()).build();
//	}

//	@Override
//	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//	}
}
