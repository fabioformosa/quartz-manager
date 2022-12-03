package it.fabioformosa;

import lombok.Generated;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer needs to deploy quartz-manager into a servlet container as a war file
 *
 * @author Fabio Formosa
 *
 */
@Generated
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(QuartzManagerDemoApplication.class);
	}

}
