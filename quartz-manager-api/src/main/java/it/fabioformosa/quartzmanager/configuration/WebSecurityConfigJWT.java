package it.fabioformosa.quartzmanager.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.fabioformosa.quartzmanager.security.auth.AuthenticationFailureHandler;
import it.fabioformosa.quartzmanager.security.auth.AuthenticationSuccessHandler;
import it.fabioformosa.quartzmanager.security.auth.LogoutSuccess;
import it.fabioformosa.quartzmanager.security.auth.RestAuthenticationEntryPoint;
import it.fabioformosa.quartzmanager.security.auth.TokenAuthenticationFilter;

/**
 *
 * @author Fabio.Formosa
 *
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigJWT extends WebSecurityConfigurerAdapter {

	@Value("${jwt.cookie}")
	private String TOKEN_COOKIE;

	//	@Autowired
	//	private CustomUserDetailsService jwtUserDetailsService;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
	private LogoutSuccess logoutSuccess;

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Value("${quartz-manager.account.user}")
	private String adminUser;

	@Value("${quartz-manager.account.pwd}")
	private String adminPwd;

	//	@Bean
	//	@Override
	//	public AuthenticationManager authenticationManagerBean() throws Exception {
	//		return super.authenticationManagerBean();
	//	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", //
				"/js/**", //
				"/img/**", //
				"/lib/**", //
				"/swagger-resources/**",  "/swagger-ui.html","/v2/api-docs", //
				"/webjars/**");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder)
			throws Exception {
		//		authenticationManagerBuilder.userDetailsService(jwtUserDetailsService)
		//		.passwordEncoder(passwordEncoder());
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		authenticationManagerBuilder.inMemoryAuthentication().withUser(adminUser).password(encoder.encode(adminPwd)).roles("ADMIN");
	}

	@Bean
	public TokenAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception {
		return new TokenAuthenticationFilter();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean();
	}

	//	@Bean
	//	public PasswordEncoder passwordEncoder() {
	//		return new BCryptPasswordEncoder();
	//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//		http.csrf().ignoringAntMatchers("/api/login", "/api/signup") //
		//		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //
		http.csrf().disable() //
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() //
		.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()
		.addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class)
		.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage("/api/login")
		.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
		.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
		.logoutSuccessHandler(logoutSuccess).deleteCookies(TOKEN_COOKIE);

	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}

}
