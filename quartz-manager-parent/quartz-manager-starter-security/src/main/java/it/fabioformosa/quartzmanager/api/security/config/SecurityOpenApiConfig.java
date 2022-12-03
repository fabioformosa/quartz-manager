package it.fabioformosa.quartzmanager.api.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import it.fabioformosa.quartzmanager.api.security.properties.JwtSecurityProperties;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Slf4j
@ConditionalOnProperty(name = "quartz-manager.oas.enabled")
@Configuration
@Generated
public class SecurityOpenApiConfig {

  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Bean("quartzManagerOpenApiCustomiser")
  public OpenApiCustomiser configureQuartzManagerOpenAPI(JwtSecurityProperties jwtSecurityProps) {
    return openAPI -> {
      if (!jwtSecurityProps.getCookieStrategy().isEnabled())
        openAPI
          .components(new Components().addSecuritySchemes(OpenAPIConfigConsts.QUARTZ_MANAGER_SEC_OAS_SCHEMA, buildBasicAuthScheme()));

      openAPI.path(QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH,
        new PathItem().post(new Operation()
          .operationId("login")
          .tags(Arrays.asList("auth"))
          .requestBody(new RequestBody().content(
            new Content().addMediaType("application/x-www-form-urlencoded", new MediaType().schema(new Schema().type("object")
              .addProperties("username", new StringSchema())
              .addProperties("password", new PasswordSchema())
              .required(Arrays.asList("username", "password"))
            ))))
          .responses(new ApiResponses().addApiResponse("200", new ApiResponse().description("JWT Token to authenticate the next requests")))
          .responses(new ApiResponses().addApiResponse("401", new ApiResponse().description("Unauthorized - Username or password are incorrect!")))
        ));
    };
  }

  private SecurityScheme buildBasicAuthScheme() {
    return new SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT")
      .description("A JWT Token in required to access this API. You can obtain a JWT Token by providing the username and password in the login API");
  }

}
