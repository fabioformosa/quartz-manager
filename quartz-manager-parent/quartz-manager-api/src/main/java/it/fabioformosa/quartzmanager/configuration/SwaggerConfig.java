package it.fabioformosa.quartzmanager.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("it.fabioformosa.quartzmanager.controllers")) //
				.build() //
				.apiInfo(apiInfo()) //
				.securitySchemes(Arrays.asList(new BasicAuth("basicAuth")))
				.securityContexts(Collections.singletonList(securityContext()));
	}

	@SuppressWarnings("rawtypes")
	private ApiInfo apiInfo() {
		String title = "QUARTZ MANAGER API";
		String description = "Quartz Manager - REST API";
		String version = "1.0.0";
		String termsOfServiceUrl = null;
		Contact contact = null;
		String license = "Apache License 2.0";
		String licenseUrl = "https://github.com/fabioformosa/quartz-manager/blob/master/LICENSE";
		List<VendorExtension> vendorExtension = Collections.emptyList();
		return new ApiInfo(title, description, version, termsOfServiceUrl, contact, license, licenseUrl, vendorExtension);
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().forPaths(PathSelectors.any()).build();
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
