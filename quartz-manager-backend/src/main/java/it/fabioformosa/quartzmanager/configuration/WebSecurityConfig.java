package it.fabioformosa.quartzmanager.configuration;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.fabioformosa.quartzmanager.security.ComboEntryPoint;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationFailureHandler;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable() //
			.antMatcher("/notifications").authorizeRequests().anyRequest().hasAnyRole("ADMIN").and()
			.httpBasic();

			//			http.antMatcher("/logs/**").authorizeRequests().anyRequest()
			//					.permitAll();
		}
	}

	@Configuration
	@Order(2)
	public static class FormWebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Resource
		private ComboEntryPoint comboEntryPoint;

		@Autowired
		private AuthenticationSuccessHandler authenticationSuccessHandler;

		@Autowired
		private AuthenticationFailureHandler authenticationFailureHandler;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			//			http.csrf().ignoringAntMatchers("/api/login", "/api/signup").and() //
			http.cors().and().csrf().disable()
			.exceptionHandling().authenticationEntryPoint(comboEntryPoint).and()//
			.authorizeRequests().anyRequest().authenticated().and()//
			.formLogin().loginPage("/api/login").successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler).and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
			.logoutSuccessUrl("/manager");
		}
	}


	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
	}

}
