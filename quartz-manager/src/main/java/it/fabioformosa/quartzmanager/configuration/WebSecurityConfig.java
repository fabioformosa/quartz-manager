package it.fabioformosa.quartzmanager.configuration;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.fabioformosa.quartzmanager.security.AjaxAuthenticationFilter;
import it.fabioformosa.quartzmanager.security.ComboEntryPoint;

@Configuration
@EnableWebSecurity
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

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.exceptionHandling().authenticationEntryPoint(comboEntryPoint).and().csrf().disable()//
					.authorizeRequests().anyRequest().authenticated().and()//
					.addFilterBefore(new AjaxAuthenticationFilter(authenticationManager()),
							UsernamePasswordAuthenticationFilter.class)//
					.formLogin().loginPage("/login").permitAll().and().logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/manager");
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
		}
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
	}

}
