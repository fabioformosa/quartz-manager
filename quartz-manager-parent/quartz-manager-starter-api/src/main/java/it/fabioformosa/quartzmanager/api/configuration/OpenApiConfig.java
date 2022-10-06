package it.fabioformosa.quartzmanager.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import it.fabioformosa.quartzmanager.api.common.config.OpenAPIConfigConsts;
import it.fabioformosa.quartzmanager.api.common.config.QuartzManagerPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI(@Autowired(required = false) SecurityDiscover securityDiscover) {
    OpenAPI openAPI = new OpenAPI()
      .info(apiInfo());

    if(securityDiscover != null)
      openAPI
        .components(new Components().addSecuritySchemes(OpenAPIConfigConsts.BASIC_AUTH_SEC_OAS_SCHEME, buildBasicAuthScheme()))
        .path(QuartzManagerPaths.QUARTZ_MANAGER_LOGIN_PATH,
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

    return openAPI;
  }

  private SecurityScheme buildBasicAuthScheme() {
    return new SecurityScheme()
      .type(SecurityScheme.Type.HTTP)
      .scheme("bearer")
      .bearerFormat("JWT");
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

}
