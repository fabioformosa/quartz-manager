package it.fabioformosa.quartzmanager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MVCConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/").setViewName("redirect:/manager");

		registry.addViewController("/templates/manager/config-form.html").setViewName("manager/config-form");
		registry.addViewController("/templates/manager/progress-panel.html")
		.setViewName("manager/progress-panel");
		registry.addViewController("/templates/manager/logs-panel.html").setViewName("manager/logs-panel");

	}

}
