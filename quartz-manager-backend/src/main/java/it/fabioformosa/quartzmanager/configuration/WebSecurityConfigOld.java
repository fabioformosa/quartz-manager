package it.fabioformosa.quartzmanager.configuration;

import javax.annotation.Resource;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.fabioformosa.quartzmanager.security.AjaxAuthenticationFilter;
import it.fabioformosa.quartzmanager.security.ComboEntryPoint;

//@Configuration
//@EnableWebSecurity
public class WebSecurityConfigOld extends WebSecurityConfigurerAdapter {

	//	@Configuration
	//	@Order(1)
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

	//	@Configuration
	//	@Order(2)
	public static class FormWebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Resource
		private ComboEntryPoint comboEntryPoint;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().ignoringAntMatchers("/api/login", "/api/signup").and() //
			.exceptionHandling().authenticationEntryPoint(comboEntryPoint).and().csrf().disable()//
			.authorizeRequests().anyRequest().authenticated().and()//
			.addFilterBefore(new AjaxAuthenticationFilter(authenticationManager()),
					UsernamePasswordAuthenticationFilter.class)//
			.formLogin().loginPage("/api/login").permitAll().and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
			.logoutSuccessUrl("/manager");
		}
	}

	//	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
	}

}
